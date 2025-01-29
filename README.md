# File-System-Analyzer
A File System Analyzer? Scannes directories, breaks down disk usage, detects duplicate files, and has a GUI with charts to better visualize information, and much more!
## Still a work in progress
currently, the code can do the following:
- scan a dictionary
- store each file name (full absolute path) and the file's size
- extract the extension of each file
- store each extension type and the total size of all files using that extension
- calculate top 10 files in size
- calculate top 10 extensions in size
- detect duplicate files using their hashcodes
- store each file and its hashcode
- store each hashcode and a list of all files with that hashcode (all duplicate files)
- export results into a txt file
- the ability to delete duplicate files
## What's next?
- custom results (user specified)
- human readable sizes (not just in bytes)
- Nice GUI
- Charts to visualize data
- mapping of common extensions to file types (.jpg, .png → "Images")
- multiple export formats (CSV or JSON)
- multithreading
- better error handling
