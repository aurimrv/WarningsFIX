 DIR_TMP=~/workspace/WarningsFIX/tmp	
 PROG=~/workspace/WarningsFIX/programs_analyzeds
 prj=$1
 TOOLS=~/workspace/WarningsFIX/tools
 SCRIPTS=~/workspace/WarningsFIX/scripts
 PARSER=~/workspace/WarningsFIX/parser

export JAVA_HOME=$TOOLS/jdk1.7.0
                export PATH=$JAVA_HOME/bin:$PATH


cd $PROG/$prj

#####################################################################################
#                              Executar ferramenta  PMD                             #
#                                                                                   #   
#####################################################################################

export JAVA_HOME==~/workspace/WarningsFIX/tools/jdk1.7.0
export PATH=$JAVA_HOME/bin:$PATH


zip $prj.zip -r source/

iniciodaexecucaodapmd=`date +%s`
			#echo -e "\tPMD"
                        mkdir $PROG/$prj/outputs/06-pmd-$prj/                       	
		        $TOOLS/PMD/bin/run.sh pmd -f xml -R rulesets/internal/all-java.xml -d $PROG/$prj/$prj.zip > $PROG/$prj/outputs/06-pmd-$prj/06-pmd-$prj.xml    
			


terminiodaexecucaodapmd=`date +%s`
soma11=`expr $terminiodaexecucaodapmd - $iniciodaexecucaodapmd`
resultado11=`expr 10800 + $soma11`
tempo11=`date -d @$resultado11 +%H:%M:%S`
#echo Tempo gasto para execução da PMD: $tempo11
info11="O-tempo-gasto-para-execução-da-PMD"
info11="$info11-$tempo11"
#bash $SCRIPTS/Store.sh $prj $info11
		

#####################################################################################
#                              Executar ferramenta  PMD                             #
#                                                                                   #   
#####################################################################################


#####################################################################################
#                                    DADOS para Parser                              #
#                                                                                   #   
#####################################################################################

iniciodaexecucaodoparserdapmd=`date +%s`

XML2=$PROG/$prj/outputs/06-pmd-$prj/06-pmd-$prj.xml
sed -i 1d $XML2
if [ `cat $XML2 | wc -l` -ne 0 ] 
then
                        bash $SCRIPTS/Parser_PMD2.sh $XML2 $PROG/$prj/$prj.zip:source/ $prj $rule2 
fi
                       


terminiodaexecucaodoparserdapmd=`date +%s`
soma12=`expr $terminiodaexecucaodoparserdapmd - $iniciodaexecucaodoparserdapmd`
resultado12=`expr 10800 + $soma11 + $soma12`
tempo12=`date -d @$resultado12 +%H:%M:%S`
#echo Tempo gasto para execução parser PMD: $tempo12
info12="O-tempo-gasto-para-execução-parser-PMD"
info12="$info12-$tempo12"
info_total_pmd="$tempo12"
echo Tempo gasto para execução da ferramenta PMD: $info_total_pmd
#bash $SCRIPTS/Store.sh $prj $info12



#####################################################################################
#                                    DADOS para Parser                              #
#                                                                                   #   
#####################################################################################




