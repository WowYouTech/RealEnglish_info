#coding=utf-8
import io
from commonUtils import copyfile
from realEngCloudUtil import parseHuaweiCloudXlsx
from realEngUtils import parseGroupInfoToJson, generateLessons
from srtToJson import convertSrtToJson


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



groupIndex = 2
groupName = 'wave_' + str(groupIndex)
originPath = '/Users/steveyang/EnglishAppProject/SnapVideos/'
infoFilePath = originPath + 'test_group_info.txt' #'_info_e.txt'
infoOutPath = originPath + 'test_group_info_json.txt'

##lesson info and words
parseGroupInfoToJson(originPath, infoFilePath, infoOutPath)

