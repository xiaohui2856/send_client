#date="20150731"
configpath="/home/edb/load"
#if [ $# -ne 2 ]
#then
#        echo "para error !"
#        echo "Usage:sh $0 yyyymmdd config_path"
#        exit 1
#fi
#day=`date --date=$date | awk '{print $4}'`
day=`date '+%Y%m%d'`
echo $day
logpath="/home/edb/load/log2"
logfile=${logpath}/${day}.log
#创建日志文件目录
if [ ! -d "$logpath" ]; then
  mkdir -p "$logpath"
fi
#判断文件是否存在
if [ ! -f "$logfile" ];then
  touch "logfile"
fi

impalaip=`cat ${configpath}/config.lst | grep  "impala-hostname" |awk '{print$2}'`
echo `date +%Y-%m-%d" "%H:%M:%S` " =============【程序开始】=============" >> ${logfile}

impala-shell -i ${impalaip} -d "edw" -q "select * from rpt_rtl_0001" -o "/home/edb/hui2.txt" -B --output_delimiter=",";

echo `date +%Y-%m-%d" "%H:%M:%S` " =============【程序结束】=============" >> ${logfile}
