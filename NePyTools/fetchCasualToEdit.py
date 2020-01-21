#coding=utf-8
import ssl
import os
import io


ssl._create_default_https_context = ssl._create_unverified_context

# 导入bmob模块
from bmob import *
# 新建一个bmob操作对象
b = Bmob("399a5f39bd4fd1092d8676ea54bb6c5a", "25bba62af9b19a4c2b4d0099bf9608c2")

def mkdir(path):
	folder = os.path.exists(path)
	if not folder:
		os.makedirs(path)

mkdir("autosub")
mkdir("srt")
autoSubPath = "autosub/auto.txt"
srtSubPath = "srt/srt.txt"
fauto = io.open(autoSubPath, "at")
fsrt = io.open(srtSubPath, "at")
fstatus = io.open('fetchStatus.txt', "w+")

i = 0
skipCount = 0
updateIndex = 0

while i < 20:

	contentList = b.find("ContentList",
			BmobQuerier().
				addWhereGreaterThan("subKind",0)
				.addWhereEqualTo("toEdit",1)
				.addWhereEqualTo("category", 5)
				.addWhereGreaterThan("cLevel", 0)
				.addWhereLessThan("cLevel", 10)
				,limit=50
				,order="cIndex"
				,skip=skipCount
			).jsonData["results"]

	pageSize = 0

	print("Fetch A Page")

	for contentItem in contentList:
		pageSize += 1
		subKind = contentItem["subKind"]
		if contentItem["cIndex"] > updateIndex:
			updateIndex = contentItem["cIndex"]

		if subKind == 1:
			fsrt.write(contentItem["originLink"])
			fsrt.write("\n")
		elif subKind == 2:
			fauto.writelines(contentItem["originLink"])
			fauto.write("\n")

	skipCount += pageSize
	i += 1
	if pageSize == 0:
		break

fstatus.write(str(updateIndex))