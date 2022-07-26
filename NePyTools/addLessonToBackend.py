#coding=utf-8
from collections import OrderedDict

import realEngUtils
from bmob import *
import ssl

from bombBackend import *
from realEngCloudUtil import parseHuaweiCloudXlsx
from realEngUtils import parseGroupInfoToJson, generateLessons
from srtToJson import convertSrtToJson

groupIndex = 2
groupName = 'wave_' + str(groupIndex)
originPath = '/Users/steveyang/EnglishAppProject/SnapVideos/'+groupName+'/'
infoFilePath = originPath + groupName + '_info_e.txt'
infoOutPath = originPath + groupName + '_info_json.txt'
assetPath = "/Users/steveyang/EnglishAppProject/RealEnglish/appRealEnglish/src/debug/assets/contents/"

# parseHuaweiCloudXlsx(originPath, groupName)

#####lesson srt
convertSrtToJson(originPath)

parseGroupInfoToJson(originPath, infoFilePath, infoOutPath)

lessonList = generateLessons(originPath, groupIndex, groupName)

addLessonsToServer(groupIndex, lessonList)

addLessonsCampItem(groupIndex)








