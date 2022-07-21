#coding=utf-8
from collections import OrderedDict

import realEngUtils
from bmob import *
import ssl


from realEngCloudUtil import parseHuaweiCloudXlsx
from realEngUtils import parseGroupInfoToJson, generateLessons
import time

#upload
bmobAppKey = "ba096f73f0149e0ed309f0f2cbb62017"
restkey = "e87334609894906ca2473b1288e8f891"

def addLessonsToServer(groupIndex, lessonList):
    bmob = Bmob(bmobAppKey, restkey)
    ssl._create_default_https_context = ssl._create_unverified_context

    bmobQue = BmobQuerier()
    bmobQue.addWhereEqualTo('groupIndex', groupIndex)
    jsonData = bmob.find("Lesson", bmobQue).jsonData
    existLessons = jsonData['results']

    existDict = OrderedDict()
    for existLesson in existLessons:
        if existLesson.__contains__('vid') and len(existLesson['vid']) > 0:
            existDict[existLesson['vid']] = existLesson

    for lesson in lessonList:
        vid = lesson[realEngUtils.keyVid];
        vidWithTitle = vid;  # + "=" + lesson['title']+'.mp4'

        # bmobQue.addWhereEqualTo(realEngUtils.keyVid, vid)
        if not existDict.__contains__(vid):
            rr = bmob.insert('Lesson', lesson)
            print('added lesson:' + vid + '\nresponse' + json.dumps(rr.jsonData) + '\n' + json.dumps(lesson) )
        else:
            el = existDict[vid]
            objectId = el['objectId']
            # data = dict(lesson)
            # data.pop('objectId', None)
            # data.pop('createdAt', None)
            # data.pop('updatedAt', None)
            rr = bmob.update('Lesson', objectId, lesson)
            print('existed lesson:' + vid + '\nresponse:' + str(rr.jsonData))


def addLessonsCampItem(groupIndex):
    bmob = Bmob(bmobAppKey, restkey)
    ssl._create_default_https_context = ssl._create_unverified_context

    bmobQue = BmobQuerier()
    bmobQue.addWhereEqualTo('type', '_lesson_camp_head')
    result = bmob.find("Lesson", bmobQue)
    jsonData = result.jsonData
    existLessons = jsonData['results']

    for existLesson in existLessons:
        if existLesson.__contains__('desc') and len(existLesson['desc']) > 0\
                and existLesson['lessonCamp'] == 'hot':

            objectId = existLesson['objectId']
            desc = existLesson['desc']
            dd = json.loads(desc)
            campItemList = dd['campItemList']

            hasCampItem = False
            for campItem in campItemList:
                if campItem['index'] == groupIndex:
                    campItem['time'] = int(time.time())
                    hasCampItem=True

            if not hasCampItem:
                acamp = OrderedDict()
                acamp['desc'] = ''
                acamp['index'] = groupIndex
                acamp['time'] = int(time.time())
                campItemList.append(acamp)

            dd['campItemList'] = campItemList
            existLesson['desc'] = json.dumps(dd)

            data = dict(existLesson)
            data.pop('objectId', None)
            data.pop('createdAt', None)
            data.pop('updatedAt', None)
            rr = bmob.update('Lesson', objectId, data)
            print('added lesson:'  + '\nresponse:' + json.dumps(rr.jsonData))

            # {"desc": "Wave", "campItemList": [{"desc": "", "index": 1, "time": 1657256488}]}




