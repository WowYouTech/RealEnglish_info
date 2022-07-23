#coding=utf-8
from commonUtils import copyfile
from realEngCloudUtil import parseHuaweiCloudXlsx
from realEngUtils import parseGroupInfoToJson, generateLessons
from srtToJson import convertSrtToJson

groupIndex = 1
groupName = 'wave_' + str(groupIndex)
originPath = '/Users/steveyang/EnglishAppProject/SnapVideos/'+groupName+'/'
infoFilePath = originPath + groupName + '_info_e.txt'
infoOutPath = originPath + groupName + '_info_json.txt'
assetPath = "/Users/steveyang/EnglishAppProject/RealEnglish/appRealEnglish/src/debug/assets/contents/"

#####lesson urls and duration
parseHuaweiCloudXlsx(originPath, groupName)

#####lesson srt
convertSrtToJson(originPath)

##lesson info and words
parseGroupInfoToJson(originPath, infoFilePath, infoOutPath)

#####generate lesson json list
generateLessons(originPath, groupIndex, groupName)

### copy to app to test
lessonFilePath = originPath + groupName + '_lessons.txt'
assetFilePath  = assetPath + 'test_lessons.txt'
copyfile(lessonFilePath, assetFilePath)






