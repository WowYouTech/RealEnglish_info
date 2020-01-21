#coding=utf-8
import io
import srt
import json
import jsonpickle

from srtMoveResultsWithSub import mkdir, mymovefile

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

class SubItem(object):

    def __init__(self, index, start, end, content, proprietary=""):
        self.index = index
        self.start = start
        self.end = end
        self.content = content
        self.proprietary = proprietary

def parseSrt(path):
    f = io.open(path, "r+")
    s = f.read()
    subs = list(srt.parse(s))
    # print(subs)
    subJson = jsonpickle.encode(subs, unpicklable=False)
    print(subJson)


srtPath = "/Users/steveyang/Data_NE_720p/Content/autosub_Sub_Done_easy/33.srt"
parseSrt(srtPath)



