"""This script finds all '.properties-MERGED' files and writes relative path, key, and value to a CSV file.
This script requires the python libraries: jproperties.  It also requires Python 3.x.
"""

from typing import List, Dict, Tuple, Callable, Iterator
import sys
import os
from propsutil import set_entry_dict, get_entry_dict
from csvutil import csv_to_records
from propentry import PropEntry
import argparse


def write_prop_entries(entries: Iterator[PropEntry], repo_path: str):
    """Writes property entry items to their expected relative path within the repo path.
    Previously existing files will be overwritten and prop entries marked as should_be_deleted will
    not be included.

    Args:
        entries (List[PropEntry]): the prop entry items to write to disk.
        repo_path (str): The path to the git repo.
    """
    items_by_file = get_by_file(entries)
    for rel_path, (entries, ignored) in items_by_file.items():
        abs_path = os.path.join(repo_path, rel_path)
        set_entry_dict(entries, abs_path)


def update_prop_entries(entries: Iterator[PropEntry], repo_path: str):
    """Updates property entry items to their expected relative path within the repo path.  The union of
    entries provided and any previously existing entries will be created.  Keys marked for deletion will be
    removed from the generated property files.

    Args:
        entries (List[PropEntry]): the prop entry items to write to disk.
        repo_path (str): The path to the git repo.
    """
    items_by_file = get_by_file(entries)
    for rel_path, (entries, to_delete) in items_by_file.items():
        abs_path = os.path.join(repo_path, rel_path)

        if os.path.isfile(abs_path):
            with open(abs_path, "rb") as f:
                prop_items = get_entry_dict(f)
        else:
            prop_items = {}

        for key_to_delete in to_delete:
            if key_to_delete in prop_items:
                del prop_items[key_to_delete]

        for key, val in entries.items():
            prop_items[key] = val

        set_entry_dict(prop_items, abs_path)


def get_by_file(entries: Iterator[PropEntry]) -> Dict[str, Tuple[Dict[str, str], List[str]]]:
    """Sorts a prop entry list by file.  The return type is a dictionary mapping 
    the file path to a tuple containing the key-value pairs to be updated and a 
    list of keys to be deleted.

    Args:
        entries (List[PropEntry]): The entries to be sorted.

    Returns:
        Dict[str, Tuple[Dict[str,str], List[str]]]: A dictionary mapping 
    the file path to a tuple containing the key-value pairs to be updated and a 
    list of keys to be deleted.
    """
    to_ret = {}
    for prop_entry in entries:
        rel_path = prop_entry.rel_path
        key = prop_entry.key
        value = prop_entry.value

        if rel_path not in to_ret:
            to_ret[rel_path] = ({}, [])

        if prop_entry.should_delete:
            to_ret[rel_path][1].append(prop_entry.key)
        else:
            to_ret[rel_path][0][key] = value

    return to_ret


def idx_bounded(num: int, max_exclusive: int) -> bool:
    return 0 <= num < max_exclusive


def get_prop_entry(row: List[str], 
                   path_idx: int = 0,
                   key_idx: int = 1,
                   value_idx: int = 2,
                   should_delete_converter: Callable[[List[str]], bool] = None,
                   path_converter: Callable[[str], str] = None) -> PropEntry:
    """Parses a PropEntry object from a row of values in a csv.

    Args:
        row (List[str]): The csv file row to parse.
        path_idx (int, optional): The column index for the relative path of the properties file. Defaults to 0.
        key_idx (int, optional): The column index for the properties key. Defaults to 1.
        value_idx (int, optional): The column index for the properties value. Defaults to 2.
        should_delete_converter (Callable[[List[str]], bool], optional): If not None, this determines if the key should
        be deleted from the row values. Defaults to None.
        path_converter (Callable[[str], str], optional): If not None, this determines the relative path to use in the
        created PropEntry given the original relative path. Defaults to None.

    Returns:
        PropEntry: The generated prop entry object.
    """

    path = row[path_idx] if idx_bounded(path_idx, len(row)) else None
    if path_converter is not None:
        path = path_converter(path)

    key = row[key_idx] if idx_bounded(key_idx, len(row)) else None
    value = row[value_idx] if idx_bounded(value_idx, len(row)) else None
    should_delete = False if should_delete_converter is None else should_delete_converter(row)
    return PropEntry(path, key, value, should_delete)


def get_prop_entries(rows: List[List[str]], 
                     path_idx: int = 0,
                     key_idx: int = 1,
                     value_idx: int = 2,
                     should_delete_converter: Callable[[List[str]], bool] = None,
                     path_converter: Callable[[str], str] = None) -> Iterator[PropEntry]:

    """Parses PropEntry objects from rows of values in a csv.

    Args:
        rows (List[List[str]]): The csv file rows to parse.
        path_idx (int, optional): The column index for the relative path of the properties file. Defaults to 0.
        key_idx (int, optional): The column index for the properties key. Defaults to 1.
        value_idx (int, optional): The column index for the properties value. Defaults to 2.
        should_delete_converter (Callable[[List[str]], bool], optional): If not None, this determines if the key should
        be deleted from the row values. Defaults to None.
        path_converter (Callable[[str], str], optional): If not None, this determines the relative path to use in the
        created PropEntry given the original relative path. Defaults to None.

    Returns:
        List[PropEntry]: The generated prop entry objects.
    """
    return map(lambda row: get_prop_entry(
            row, path_idx, key_idx, value_idx, should_delete_converter, path_converter), 
            rows)


def get_should_deleted(row_items: List[str], requested_idx: int) -> bool:
    """If there is a value at row_items[requested_idx] and that value is not empty, then this will return true.

    Args:
        row_items (List[str]): The row items.
        requested_idx (int): The index specifying if the property should be deleted.

    Returns:
        bool: True if the row specifies it should be deleted.
    """
    if idx_bounded(requested_idx, len(row_items)) and len((row_items[requested_idx].strip())) > 0:
        return True
    else:
        return False


def get_new_rel_path(orig_path: str, new_filename: str) -> str:
    """Obtains a new relative path.  This tries to determine if the provided path is a directory or filename (has an
    extension containing '.') then constructs the new path with the old parent directory and the new filename.

    Args:
        orig_path (str): The original path.
        new_filename (str): The new filename to use.

    Returns:
        str: The new path.
    """
    potential_parent_dir, orig_file = os.path.split(orig_path)
    parent_dir = orig_path if '.' not in orig_file else potential_parent_dir
    return os.path.join(parent_dir, new_filename)


def main():
    parser = argparse.ArgumentParser(description='Updates properties files in the autopsy git repo.')

    parser.add_argument(dest='csv_file', type=str, help='The path to the csv file.')
    parser.add_argument(dest='repo_path', type=str, help='The path to the repo.')
    parser.add_argument('-p', '--path_idx', dest='path_idx', action='store', type=int, default=0, required=False, 
                        help='The column index in the csv file providing the relative path to the properties file.')
    parser.add_argument('-k', '--key_idx', dest='key_idx', action='store', type=int, default=1, required=False, 
                        help='The column index in the csv file providing the key within the properties file.')
    parser.add_argument('-v', '--value_idx', dest='value_idx', action='store', type=int, default=2, required=False, 
                        help='The column index in the csv file providing the value within the properties file.')
    parser.add_argument('-d', '--should_delete_idx', dest='should_delete_idx', action='store', type=int, default=None,
                        required=False, help='The column index in the csv file providing whether or not the file ' +
                                             'should be deleted.  Any non-blank content will be treated as True.')
    parser.add_argument('-c', '--commit_idx', dest='latest_commit_idx', action='store', type=int, default=3,
                        required=False, help='The column index in the csv file providing the commit for which this ' +
                                             'update applies. The commit should be located in the header row.  ')
    parser.add_argument('-f', '--file_rename', dest='file_rename', action='store', type=str, default=None,
                        required=False, help='If specified, the properties file will be renamed to the argument' +
                                             ' preserving the specified relative path.')
    parser.add_argument('--has_no_header', dest='has_no_header', action='store_true', default=False, required=False, 
                        help='Specify whether or not there is a header within the csv file.')
    parser.add_argument('-o', '--should_overwrite', dest='should_overwrite', action='store_true', default=False,
                        required=False, help="Whether or not to overwrite the previously existing properties files" +
                                             " ignoring previously existing values.")


    args = parser.parse_args()

    repo_path = args.repo_path
    input_path = args.csv_file
    path_idx = args.path_idx
    key_idx = args.key_idx
    value_idx = args.value_idx
    has_header = not args.has_no_header
    overwrite = args.should_overwrite

    if args.should_delete_idx is None:
        should_delete_converter = None
    else:
        def should_delete_converter(row_items: List[str]):
            return get_should_deleted(row_items, args.should_delete_idx)

    if args.file_rename is None:
        path_converter = None
    else:
        def path_converter(orig_path: str):
            return get_new_rel_path(orig_path, args.file_rename)

    all_items = list(csv_to_records(input_path, has_header))
    prop_entries = get_prop_entries(all_items, path_idx, key_idx, value_idx, should_delete_converter, path_converter)

    if overwrite:
        write_prop_entries(prop_entries, repo_path)
    else:
        update_prop_entries(prop_entries, repo_path)

    sys.exit(0)


if __name__ == "__main__":
    main()
