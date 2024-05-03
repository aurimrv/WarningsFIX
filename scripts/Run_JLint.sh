
        DIR_TMP=~/workspace/WarningsFIX/tmp	
	PROG=~/workspace/WarningsFIX/programs_analyzeds
        prj=$1
	TOOLS=~/workspace/WarningsFIX/tools
        SCRIPTS=~/workspace/WarningsFIX/scripts
        PARSER=~/workspace/WarningsFIX/parser

export JAVA_HOME=$TOOLS/jdk1.7.0
                export PATH=$JAVA_HOME/bin:$PATH

			

#####################################################################################
#                              Executar ferramenta JLint                            #
#                                                                                   #   
#####################################################################################

cd $PROG/$prj

iniciodaexecucaodajlint=`date +%s`

TIPOS_AVISOS="synchronization 
deadlock 
race_condition 
wait_nosync
inheritance
super_finalize
not_overridden
field_redefined
shadow_local
data_flow
null_reference
zero_operand
zero_result
redundant
overflow
incomp_case
short_char_cmp
string_cmp
weak_cmp
domain
truncation
bounds 
"

mkdir $PROG/$prj/outputs/05-jlint-$prj 
for java9 in $(find source -name "*.java"); do
nome5=${java9//\//-}
class=${java9/%java/class}

			# Executando JLint
			#echo -e "\tJLint"
                        mkdir $PROG/$prj/outputs/05-jlint-$prj/05-jlint-$nome5 
                        for TYPE in $TIPOS_AVISOS 
			do jlint -all +$TYPE $class >& $PROG/$prj/outputs/05-jlint-$prj/05-jlint-$nome5/05-jlint-$nome5-$TYPE.txt 
                        done
done

terminiodaexecucaodajlint=`date +%s`
soma9=`expr $terminiodaexecucaodajlint - $iniciodaexecucaodajlint`
resultado9=`expr 10800 + $soma9`
tempo9=`date -d @$resultado9 +%H:%M:%S`
#echo Tempo gasto para execução da JLint: $tempo9
info9="O-tempo-gasto-para-execução-da-JLint"
info9="$info9-$tempo9"
#bash $SCRIPTS/Store.sh $prj $info9

#####################################################################################
#                              Executar ferramenta JLint                            #
#                                                                                   #   
#####################################################################################

#####################################################################################
#                                    DADOS para Parser                              #
#                                                                                   #   
#####################################################################################

iniciodaexecucaodoparserdajlint=`date +%s`

for java10 in $(find source -name "*.java"); do
nome6=${java10//\//-}
                        for TYPE2 in $TIPOS_AVISOS                       
                        do 
TXT2=$PROG/$prj/outputs/05-jlint-$prj/05-jlint-$nome6/05-jlint-$nome6-$TYPE2.txt
if [ `cat $TXT2 | wc -l` -ne 0 ] 
then
                        
                        bash $SCRIPTS/Parser_JLint.sh $TXT2 $prj $nome6 $TYPE2 
fi
                        done

done

terminiodaexecucaodoparserdajlint=`date +%s`
soma10=`expr $terminiodaexecucaodoparserdajlint - $iniciodaexecucaodoparserdajlint`
resultado10=`expr 10800 + $soma9 + $soma10`
tempo10=`date -d @$resultado10 +%H:%M:%S`
#echo Tempo gasto para execução parser JLint: $tempo10 
info10="O-tempo-gasto-para-execução-parser-JLint"
info10="$info10-$tempo10"
info_total_jlint="$tempo10"
echo Tempo gasto para execução da ferramenta Jlint: $info_total_jlint
#bash $SCRIPTS/Store.sh $prj $info10

#####################################################################################
#                                    DADOS para Parser                              #
#                                                                                   #   
#####################################################################################
