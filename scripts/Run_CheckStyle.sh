        DIR_TMP=~/workspace/WarningsFIX/tmp	
	PROG=~/workspace/WarningsFIX/programs_analyzeds
        prj=$1
	TOOLS=~/workspace/WarningsFIX/tools
        SCRIPTS=~/workspace/WarningsFIX/scripts
        PARSER=~/workspace/WarningsFIX/parser

export JAVA_HOME=$TOOLS/jdk1.7.0
                export PATH=$JAVA_HOME/bin:$PATH
#####################################################################################
#                              Executar ferramenta  Checkstyle                      #
#                                                                                   #   
#####################################################################################

iniciodaexecucaodacheckstyle=`date +%s`
mkdir $PROG/$prj/outputs/04-checkstyle-$prj 
java -jar $TOOLS/Checkstyle/checkstyle-5.6-all.jar -c $TOOLS/Checkstyle/sun_checks.xml -f xml -r $PROG/$prj/source > $PROG/$prj/outputs/04-checkstyle-$prj/04-checkstyle.xml 


terminiodaexecucaodacheckstyle=`date +%s`
soma7=`expr $terminiodaexecucaodacheckstyle - $iniciodaexecucaodacheckstyle`
resultado7=`expr 10800 + $soma7`
tempo7=`date -d @$resultado7 +%H:%M:%S`
#echo Tempo gasto para execução da Checkstyle: $tempo7
info7="O-tempo-gasto-para-execução-da-Checkstyle"
info7="$info7-$tempo7"
#bash $SCRIPTS/Store.sh $prj $info7

#####################################################################################
#                              Executar ferramenta Checkstyle                       #
#                                                                                   #   
#####################################################################################

#####################################################################################
#                                    DADOS para Parser                              #
#                                                                                   #   
#####################################################################################

iniciodaexecucaodoparserdacheckstyle=`date +%s`

      
XMLLL=$PROG/$prj/outputs/04-checkstyle-$prj/04-checkstyle.xml
if [ `cat $XMLLL | wc -l` -ne 0 ] 
then
                        bash $SCRIPTS/Parser_Checkstyle2.sh $XMLLL $prj $PROG/$prj/source 
fi

terminiodaexecucaodoparserdacheckstyle=`date +%s`
soma8=`expr $terminiodaexecucaodoparserdacheckstyle - $iniciodaexecucaodoparserdacheckstyle`
resultado8=`expr 10800 + $soma7 + $soma8`
tempo8=`date -d @$resultado8 +%H:%M:%S`
#echo Tempo gasto para execução parser Checkstyle: $tempo8
info8="O-tempo-gasto-para-execução-parser-Checkstyle"
info8="$info8-$tempo8"
info_total_checkstyle="$tempo8"
echo Tempo gasto para execução da ferramenta Checkstyle: $info_total_checkstyle
#bash $SCRIPTS/Store.sh $prj $info8


#####################################################################################
#                                    DADOS para Parser                              #
#                                                                                   #   
#####################################################################################

