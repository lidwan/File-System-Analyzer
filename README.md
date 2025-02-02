# File-System-Analyzer
A File System Analyzer? Scannes directories, breaks down disk usage, detects duplicate files, and has a GUI with charts to better visualize information, and much more!
## What the program can currently do:
currently, the code can do the following (CLI, not GUI.. yet):
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
- mapping of common extensions to file types (.jpg, .png → "Images")
- human-readable sizes (not just in bytes)
- multithreading
- custom results (user specified)
## What am I working on right now?
- Nice GUI (w/ JavaFX)
## What's next?
- better error handling
- Charts to visualize data

