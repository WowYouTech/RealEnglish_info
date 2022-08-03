#coding=utf-8
from commonUtils import copyfile
from realEngCloudUtil import parseHuaweiCloudXlsx
from realEngUtils import *
from srtToJson import convertSrtToJson

groupIndex = 2
groupName = 'wave_' + str(groupIndex)
originPath = '/Users/steveyang/EnglishAppProject/SnapVideos/'+groupName+'/'
infoFilePath = originPath + groupName + '_info_e.txt'
infoOutPath = originPath + groupName + '_info_json.txt'
assetPath = "/Users/steveyang/EnglishAppProject/RealEnglish/appRealEnglish/src/debug/assets/contents/"

#####lesson urls and duration
# parseHuaweiCloudXlsx(originPath, groupName)
convertSrtToJson(originPath)#####lesson srt

parseGroupInfoToJson(originPath, infoFilePath, infoOutPath)##lesson info and words

generateLessons(originPath, groupIndex, groupName)#####generate lesson json list
# generateLessonsHuawei(originPath, groupIndex, groupName)#####generate lesson json list

### copy to app to test
lessonFilePath = originPath + groupName + '_lessons.txt'
assetFilePath  = assetPath + 'test_lessons.txt'
copyfile(lessonFilePath, assetFilePath)






