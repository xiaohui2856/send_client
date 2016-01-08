##需要传入3个参数，应该分别为dataType，dataVersion，filePath
##例如：java -jar client.jar Jxj001 20150603 /home/edb/hui2.txt
echo `date +%Y-%m-%d" "%H:%M:%S` " =============【upload file start】=============" 
java -jar client.jar Jxj001 20150603 /home/edb/hui2.txt
echo `date +%Y-%m-%d" "%H:%M:%S` " =============【upload file end】=============" 