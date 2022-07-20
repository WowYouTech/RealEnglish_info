#coding=utf-8
import os
import fnmatch

# pjtPath = '/Users/steveyang/Data_NE_Mobile/Content/autosub_to_process_easy/'
# pjtPath = '/Users/steveyang/Downloads/asr/'

contentPath = '/Users/steveyang/Data_NE_720p'

# def renameSrts(parentPath):
# 	for filename in os.listdir(parentPath):
# 		if os.path.isdir(parentPath + filename):
# 			renameSrts(parentPath+filename+'/')
#
# 		elif filename.endswith('.en.srt'):
# 			target = filename.replace('.en.srt', '.srt')
# 			if os.path.isfile(parentPath + filename):
# 				os.rename(parentPath + filename, parentPath + target)
#
# renameSrts(contentPath)

def findReplace(directory, find, replace, filePattern):
    for path, dirs, files in os.walk(os.path.abspath(directory)):
        for filename in fnmatch.filter(files, filePattern):
            filepath = os.path.join(path, filename)
            with open(filepath) as f:
                s = f.read()
            s1 = s.replace(find, replace)
            with open(filepath, "w") as f:
                f.write(s1)

findReplace(contentPath, "’", "'", "*.srt")



