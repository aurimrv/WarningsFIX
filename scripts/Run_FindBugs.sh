        DIR_TMP=~/workspace/WarningsFIX/tmp	
	PROG=~/workspace/WarningsFIX/programs_analyzeds
        prj=$1
	TOOLS=~/workspace/WarningsFIX/tools
        SCRIPTS=~/workspace/WarningsFIX/scripts
        PARSER=~/workspace/WarningsFIX/parser



		# Redefinindo para JDK 1.7
		export JAVA_HOME=$TOOLS/jdk1.7.0
                export PATH=$JAVA_HOME/bin:$PATH

#####################################################################################
#                              Executar ferramenta   FindBugs                       #
#                                                                                   #   
#####################################################################################

cd $PROG
find $prj/ -name *.java >  $DIR_TMP/$prj-java.txt

iniciodaexecucaodafindbugs=`date +%s`

		# Executando FindBugs
		#echo -e "\tFindBugs"
                 
		$TOOLS/findbugs-2.0.2/bin/findbugs -textui  -nested:false -effort:max -sortByClass -low  -jvmArgs "-Duser.language=pt_BR" -xml:withMessages -output $PROG/$prj/outputs/02-findbugs-$prj.xml $PROG/$prj/$prj.jar 

#terminiodaexecucaodafindbugs=`date +%s`
#soma3=`expr $terminiodaexecucaodafindbugs - $iniciodaexecucaodafindbugs`
#resultado3=`expr 10800 + $soma3`
#tempo3=`date -d @$resultado3 +%H:%M:%S`
#echo Tempo gasto para execução da FindBugs: $tempo3
#info3="O-Tempo-gasto-para-execução-da-FindBugs"
#info3="$info3-$tempo3"
#bash $SCRIPTS/Store.sh $prj $info3


#####################################################################################
#                              Executar ferramenta   FindBugs                       #
#                                                                                   #   
#####################################################################################


#####################################################################################
#                                    DADOS para Parser                              #
#                                                                                   #   
#####################################################################################

iniciodaexecucaodoparserdafindbugs=`date +%s`

XML=$PROG/$prj/outputs/02-findbugs-$prj.xml
PROGRAMA=$prj
VERSAO=.
ARQUIVO=$prj
USER=postgres
PASSWORD=postgres
if [ `cat $XML | wc -l` -ne 0 ] 
then 
bash  $SCRIPTS/Parser_FindBugs.sh $XML $PROGRAMA $VERSAO $ARQUIVO $USER $PASSWORD $DIR_TMP 
fi


terminiodaexecucaodoparserdafindbugs=`date +%s`
soma4=`expr $terminiodaexecucaodoparserdafindbugs - $iniciodaexecucaodoparserdafindbugs`
resultado4=`expr 10800 + $soma3 + $soma4`
tempo4=`date -d @$resultado4 +%H:%M:%S`
#echo Tempo gasto para execução parser FindBugs: $tempo4 
info4="O-tempo-gasto-para-execução-parser-FindBugs"
info4="$info4-$tempo4"
info_total_findbugs="$tempo4"
echo Tempo gasto para execução da ferramenta FindBugs: $info_total_findbugs 
#bash $SCRIPTS/Store.sh $prj $info4



#####################################################################################
#                                    DADOS para Parser                              #
#                                                                                   #   
#####################################################################################
