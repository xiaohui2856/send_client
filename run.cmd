rem @echo off
rem ��Ҫ����3��������Ӧ�÷ֱ�ΪdataType��dataVersion��filePath
rem ���磺java -jar client.jar Jxj001 20150603 d:\rpt_rtl_0001.txt
set info=""
java -jar client.jar Jxj001 20150603 d:\rpt_rtl_0001.txt >> %info%
echo %info%
pause