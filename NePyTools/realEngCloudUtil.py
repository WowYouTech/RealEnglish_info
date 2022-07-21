#coding=utf-8
import io

import realEngUtils
import srt_yy
import jsonpickle
import os
import os.path
import shutil
import xlrd
import openpyxl
import pandas as pd


from collections import OrderedDict
import simplejson as json

data_list = []
colIndexList = [1, 9, 17, 18]
colList = ['vid', 'coverUrl','contentUrl','duration']


def parseHuaweiCloudXlsx(path, fileName):
    xlsxFileName = fileName + '.xlsx'
    inputFilePath = path + xlsxFileName
    outFilePath = path + fileName + realEngUtils.cloudInfoTail

    df = pd.read_excel(inputFilePath)
    for index, row in df.iterrows():
        data = OrderedDict()
        row_values = row.values
        for colNum in range(0, len(colIndexList)):
            vv = row_values[colIndexList[colNum]]
            ss = str(vv)
            cc = colList[colNum]
            if cc == 'vid':
                dd = ss.split('=')
                ss = dd[0]

            if cc == 'duration':
                dd = ss.split(':')
                ss = dd[1] + ':' + dd[2]

            data[cc] = ss
        data_list.append(data)

    # Serialize the list of dicts to JSON
    j = json.dumps(data_list)

    # Write to file
    with open(outFilePath, 'w+') as f:
        f.write(j)


#test
# originPath = "/Users/steveyang/EnglishAppProject/SnapVideos/wave_2/"
# groupname = 'wave_2'

# parseHuaweiCloudXlsx(originPath,groupname)


