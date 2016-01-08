rem @echo off
rem 需要传入3个参数，应该分别为dataType，dataVersion，filePath
rem 例如：java -jar client.jar Jxj001 20150603 d:\rpt_rtl_0001.txt
set info=""
java -jar client.jar Jxj001 20150603 d:\rpt_rtl_0001.txt >> %info%
echo %info%
pause