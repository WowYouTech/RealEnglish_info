#coding=utf-8
import os
import io
import time
import shutil

from srtMoveResultsWithSub import mkdir, mymovefile

pjtPath = '/Users/steveyang/Data_Native_English/'

head = 'https://www.youtube.com/watch?v='
toProcessPathAuto = 'Content/autosub_to_process/'
toProcessPathSrt = 'Content/srt_to_process/'
noSub = 'Content/nosub/'
autosub = 'Content/autosub/'
srt = 'Content/srt/'

tmpAuto = 'autosub_to_process/'
tmpSrt = 'srt_to_process/'
tmpNoSub = 'nosub/'

# def moveFileForTypes(path, fileName, dstPath):
# 	if fileName.endswith('.mp4') and fileName.startswith(vid):
# 		mymovefile(path+fileName, dstPath + fileName)
# 	if fileName.endswith('.srt') and fileName.startswith(vid):
# 		mymovefile(path+fileName, dstPath + fileName)
# 	if fileName.endswith('.jpg') and fileName.startswith(vid):
# 		mymovefile(path+fileName, dstPath + fileName)
#
#
# def moveFileDownloaded(vid, path):
# 	mkdir(path+tmpAuto)
# 	mkdir(path+tmpSrt)
# 	mkdir(path+tmpNoSub)
#
# 	for filename in os.listdir(toProcessPathAuto):
# 		if filename.startswith(vid):
# 			moveFileForTypes(toProcessPathAuto, filename,path+tmpAuto+filename)
#
# 	for filename in os.listdir(toProcessPathSrt):
# 		if filename.startswith(vid):
# 			moveFileForTypes(toProcessPathSrt, filename, path+tmpSrt + filename)
#
# 	for filename in os.listdir(noSub):
# 		if filename.startswith(vid):
# 			moveFileForTypes(noSub, filename, path+tmpNoSub + filename)
#
#
# fauto = io.open("casual.txt", "r")
# lines = fauto.readlines()
# for line in lines:
# 	vid = line.replace(head,'')
# 	vid = vid.replace('\n', '')
# 	moveFileDownloaded(vid,"Casual/")
#
# fauto = io.open("casualToEdit.txt", "r")
# lines = fauto.readlines()
# for line in lines:
# 	vid = line.replace(head, '')
# 	vid = vid.replace('\n', '')
# 	moveFileDownloaded(vid,"CasualToEdit/")



#############- Clean not in list files
chPath = "zh-Hans/"
cleanedPath = "not_in_list/"
fAllList = io.open(pjtPath+"allList.txt", "r")
allListLines = fAllList.readlines()

def isFileInList(fileName,output) -> bool:

	for line in allListLines:
		vid = line.replace(head, '')
		vid = vid.replace('\n', '')
		if fileName.startswith(vid):
			if fileName.endswith('.mp4'):
				output.write(line)
			return True
	return False

def cleanFileForTypes(originPath, fileName, dstPath,output):
	if fileName.endswith('.mp4') or fileName.endswith('.srt') or fileName.endswith('.jpg'):
		if not isFileInList(fileName,output):
			mymovefile(originPath + fileName, dstPath + fileName)

def cleanFileNotInList(originPath, outputFileName, dstPath):
	tmpAutoCh = tmpAuto + chPath
	tmpSrtCh = tmpSrt + chPath
	tmpNoSubCh = tmpNoSub + chPath
	mkdir(originPath+tmpAutoCh)
	mkdir(originPath+tmpSrtCh)
	mkdir(originPath+tmpNoSubCh)
	mkdir(dstPath)

	output = io.open(pjtPath + outputFileName, "w+")

	for filename in os.listdir(originPath + tmpAuto):
		cleanFileForTypes(originPath + tmpAuto, filename, dstPath,output)

	for filename in os.listdir(originPath + tmpSrt):
		cleanFileForTypes(originPath + tmpSrt,filename, dstPath,output)

	for filename in os.listdir(originPath + tmpNoSub):
		cleanFileForTypes(originPath + tmpNoSub, filename, dstPath,output)

	for filename in os.listdir(originPath + tmpAutoCh):
		cleanFileForTypes(originPath + tmpAutoCh,filename, dstPath,output)

	for filename in os.listdir(originPath + tmpSrtCh):
		cleanFileForTypes(originPath + tmpSrtCh,filename, dstPath,output)

	for filename in os.listdir(originPath + tmpNoSubCh):
		cleanFileForTypes(originPath + tmpNoSubCh,filename, dstPath,output)


cleanFileNotInList(pjtPath+'Content/','outComplexNoEdit.txt',pjtPath+cleanedPath)
cleanFileNotInList(pjtPath+'Casual/','outCasualNoEdit.txt',pjtPath+cleanedPath)
cleanFileNotInList(pjtPath+'CasualToEdit/','outCasualToEdit.txt',pjtPath+cleanedPath)