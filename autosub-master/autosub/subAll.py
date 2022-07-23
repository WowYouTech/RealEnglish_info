import os
import autosub
import time
import shutil
import ffmpeg

if __name__ == '__main__':
    groupIndex = 2
    groupName = 'wave_' + str(groupIndex)
    originPath = '/Users/steveyang/EnglishAppProject/SnapVideos/' + groupName + '/'

    filename = 'short_2_7=😂Before kids vs after kids.mp4'

    print(filename)

    sub = originPath + filename.replace('.mp4', '_autosub.srt')

    print(sub)
    print("generate sub " + filename)

    subtitle_file_path = autosub.generate_subtitles(
        source_path=originPath + filename,
        concurrency=10,
        output=sub,
    )
    print("Subtitles file created at {}".format(subtitle_file_path))



# for filename in os.listdir(originPath):
#     if filename.endswith('.mp4'):
#         print(filename)
#         sub = originPath + filename.replace('.mp4', '_autosub.srt')
#
#         print(sub)
#
#         print("generate sub " + filename)
#         subtitle_file_path = autosub.generate_subtitles(
#             source_path=originPath+filename,
#             concurrency=10,
#             output=sub,
#         )
#         print("Subtitles file created at {}".format(subtitle_file_path))
#
#         # time.sleep(2)
#         break



