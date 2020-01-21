#coding=utf-8
import os
import io
import fnmatch


from srtMoveResultsWithSub import mkdir, mymovefile

# pjtPath = '/Users/steveyang/Data_NE_Mobile/Content/autosub_to_process_easy/'
# pjtPath = '/Users/steveyang/Downloads/asr/'
#
# def cleanFileNotInList(originPath, outputFileName):
#
# 	output = io.open(pjtPath + outputFileName, "w+")
#
# 	for filename in os.listdir(originPath):
# 		if filename.endswith('mp4'):
# 			jpgf = filename.replace('mp4','jpg')
# 			if not os.path.exists(pjtPath+jpgf):
# 				output.write(filename)
# 				output.write('\n')
#
# 	output.close()
#
#
# postName = '_0_en.srt'

# cleanFileNotInList(pjtPath,'outNoEdit.txt')

# for filename in os.listdir(pjtPath):
# 	if filename.endswith('mp4'):
# 		vid = filename.split('=',1)[0]
# 		srt = vid + postName
#
# 		target = filename.replace('.mp4','.en.srt')
#
# 		if os.path.isfile(pjtPath+srt):
# 			os.rename(pjtPath+srt,pjtPath+target)

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

# def findReplace(directory, find, replace, filePattern):
#     for path, dirs, files in os.walk(os.path.abspath(directory)):
#         for filename in fnmatch.filter(files, filePattern):
#             filepath = os.path.join(path, filename)
#             with open(filepath) as f:
#                 s = f.read()
#             s1 = s.replace(find, replace)
#             with open(filepath, "w") as f:
#                 f.write(s1)
#
# findReplace(contentPath, "’", "'", "*.srt")


path = '/Users/steveyang/NativeEnglish/'
w1 = "words_10k_houge.txt"
whard = "words_hard_10k.txt"
fhard = io.open(path+whard, "r")

def existInList(listItems:list, word:str)->bool:
    for line in listItems:
        if line.lower() == word.lower():
            return True
    return False


fwords = io.open(path + w1, "r")
words = fwords.readlines()

fHardNotInHouge = io.open(path + "HardNotInHouge.txt", "w+")
lines = fhard.readlines()
for item in lines:
    if not existInList(words, item):
        fHardNotInHouge.write(item)
fHardNotInHouge.close()

# fleft = io.open(path + "output_left.txt", "w+")
# fhard = io.open(path + "output_hard.txt", "w+")
# lines = fhard.readlines()
# for wordItem in words:
#     if existInList(lines, wordItem):
#         fhard.write(wordItem)
#     else:
#         fleft.write(wordItem)
# fleft.close()
# fhard.close()

# fOldList = io.open(path + "words_10k.txt")
# oldList = fOldList.readlines()
# def existInList(oldList, word:str)->bool:
#     for line in oldList:
#         if line.lower() == word.lower():
#             return True
#     return False
#
# fleft = io.open(path + "output_left.txt", "r")
# fleftFiltered = io.open(path + "output_left_filtered.txt", "w+")
# leftList = fleft.readlines()
# for item in leftList:
#     if not existInList(oldList, item):
#         fleftFiltered.write(item)
# fleftFiltered.close()

