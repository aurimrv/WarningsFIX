#####################################################
#####    inserir dados do PMD no BD             #####  
#####################################################
echo inserindo dados programa $2 sobre a regra $4 da ferramenta PMD no BD

JARS_DIR=$WARNINGSFIX_HOME/parser/PMD/lib 
DIR_TMP=$WARNINGSFIX_HOME/tmp					   
cd $JARS_DIR

ls *.jar >& $DIR_TMP/libs-jar-pmd.txt

JARS="";

for JAR in `cat $DIR_TMP/libs-jar-pmd.txt` 
do JARS=$JARS_DIR/$JAR:$JARS
done


cd $WARNINGSFIX_HOME/parser/PMD/bin
CLASSPATH=$CLASSPATH:$JARS
java -cp $CLASSPATH  parse.main $1 $3 $2

#####################################################
#####           Terminando                      #####  
#####################################################
