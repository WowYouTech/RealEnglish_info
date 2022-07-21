#coding=utf-8
import io

import srt_yy
import jsonpickle
import os
import os.path

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
    subs = list(srt_yy.parse(s))

    subJson = jsonpickle.encode(subs, unpicklable=False)
    f.close()
    return subJson

def convertSrtToJson(path):
    for filename in os.listdir(path):
        if filename.endswith('.srt'):
            outFileName = filename.replace('.srt','.txt')
            subJson = parseSrt(path+filename)
            ss = str(subJson)
            print(ss)
            with open(path+outFileName, 'w+') as outfile:
                outfile.write(ss)

# originPath = "/Users/steveyang/EnglishAppProject/SnapVideos/wave_2/"
# assetPath = "/Users/steveyang/EnglishAppProject/RealEnglish/appRealEnglish/src/debug/assets/contents/"
# convertSrtToJson(originPath)



