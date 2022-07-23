#coding=utf-8
from collections import OrderedDict

import realEngUtils
from bmob import *
import ssl

from bombBackend import *
from realEngCloudUtil import parseHuaweiCloudXlsx
from realEngUtils import parseGroupInfoToJson, generateLessons


groupIndex = 1
groupName = 'wave_' + str(groupIndex)
originPath = '/Users/steveyang/EnglishAppProject/SnapVideos/'+groupName+'/'
infoFilePath = originPath + groupName + '_info_e.txt'
infoOutPath = originPath + groupName + '_info_json.txt'
assetPath = "/Users/steveyang/EnglishAppProject/RealEnglish/appRealEnglish/src/debug/assets/contents/"

# parseHuaweiCloudXlsx(originPath, groupName)

realEngUtils.generateGroupInfoFile(originPath, groupName)

# parseGroupInfoToJson(originPath, infoFilePath, infoOutPath)
lessonList = generateLessons(originPath, groupIndex, groupName)

updateWordsAndSrtToServer(groupIndex, lessonList)








