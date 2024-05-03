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
#                              Executar ferramenta  JCSC                            #
#                                                                                   #   
#####################################################################################
iniciodaexecucaodajcsc=`date +%s`
mkdir $PROG/$prj/outputs/03-jcsc-$prj 
		# Encontrar lista dos fontes
		for java in $(find source -name "*.java"); do                       
			
			# Substituindo / por - no nome
                        echo $java
			nome=${java//\//-}

			#echo "----- Arquivo: $java -----"
			# Executando JCSC
			echo -e "\tJCSC"
		$TOOLS/JCSC/bin/jcsc.sh -r $TOOLS/JCSC/rules/jcsc.jcsc.xml $java > $PROG/$prj/outputs/03-jcsc-$prj/03-jcsc-$nome.txt

done

terminiodaexecucaodajcsc=`date +%s`
soma5=`expr $terminiodaexecucaodajcsc - $iniciodaexecucaodajcsc`
resultado5=`expr 10800 + $soma5`
tempo5=`date -d @$resultado5 +%H:%M:%S`
#echo Tempo gasto para execução da JCSC: $tempo5
info5="O-tempo-gasto-para-execução-da-JCSC"
info5="$info5-$tempo5"
#bash $SCRIPTS/Store.sh $prj $info5

#####################################################################################
#                              Executar ferramenta  JCSC                            #
#                                                                                   #   
#####################################################################################


#####################################################################################
#                                    DADOS para Parser                              #
#                                                                                   #   
#####################################################################################
iniciodaexecucaodoparserdajcsc=`date +%s`

for java2 in $(find source -name "*.java"); do
nome2=${java2//\//-}
TXT3=$PROG/$prj/outputs/03-jcsc-$prj/03-jcsc-$nome2.txt 
if [ `cat $TXT3 | wc -l` -ne 0 ] 
then                        
                        bash $SCRIPTS/Parser_JCSC.sh $TXT3 $prj $nome2 
fi

done

terminiodaexecucaodoparserdajcsc=`date +%s`
soma6=`expr $terminiodaexecucaodoparserdajcsc - $iniciodaexecucaodoparserdajcsc`
resultado6=`expr 10800 + $soma5 + $soma6`
tempo6=`date -d @$resultado6 +%H:%M:%S`
#echo Tempo gasto para execução parser JCSC: $tempo6 
info6="O-tempo-gasto-para-execução-parser-JCSC"
info6="$info6-$tempo6"
info_total_jcsc="$tempo6"
echo Tempo gasto para execução da ferramenta JCSC: $info_total_jcsc 
#bash $SCRIPTS/Store.sh $prj $info6

#####################################################################################
#                                    DADOS para Parser                              #
#                                                                                   #   
#####################################################################################
