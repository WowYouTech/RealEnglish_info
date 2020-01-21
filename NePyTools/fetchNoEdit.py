#coding=utf-8
import ssl
import os
import io


ssl._create_default_https_context = ssl._create_unverified_context

# 导入bmob模块
from bmob import *
# 新建一个bmob操作对象
b = Bmob("399a5f39bd4fd1092d8676ea54bb6c5a", "25bba62af9b19a4c2b4d0099bf9608c2")

# 插入一行数据，原子计数、Pointer
# print(
# 	b.insert(
# 		'Feedback', # 表名
# 		BmobUpdater.increment(
# 			"count", # 原子计数key
# 			2, # 原子计数值
# 			{ # 额外信息
# 				"content": "测试python",
# 				"user": BmobPointer("_User", "xxx"), # Pointer类型
# 				"date": BmobDate(1545098009351) ## Date类型
# 			}
# 		)
# 	).jsonData # 输出json格式的内容
# )

#cStatus: 0:none,1:sorted,2:processing,3:processed,4:verified

def mkdir(path):
	folder = os.path.exists(path)
	if not folder:
		os.makedirs(path)

mkdir("autosub")
mkdir("srt")
autoSubPath = "autosub/auto.txt"
srtSubPath = "srt/srt.txt"
fauto = io.open(autoSubPath, "w+")
fsrt = io.open(srtSubPath, "w+")
fstatus = io.open('fetchStatus.txt', "w+")

i = 0
skipCount = 0
lastIndex = 0

fetchStatus = b.find(
	"ContentFetchStatus",
	BmobQuerier()
		.addWhereEqualTo("toEdit", 0)
).jsonData["results"]

fetchStatusItem = fetchStatus[0]
lastIndex = fetchStatusItem["lastIndex"]
updateIndex = lastIndex

while i < 2000:

	contentList = b.find("ContentList",
			BmobQuerier().
				addWhereGreaterThan("subKind",0)
				.addWhereEqualTo("toEdit",0)
				.addWhereGreaterThan("cIndex",lastIndex)
				.addWhereGreaterThan("cLevel", 0)
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

if updateIndex > lastIndex:
	result = b.update("ContentFetchStatus", fetchStatusItem["objectId"], {
			"lastIndex": updateIndex
	})
fstatus.write(str(updateIndex))