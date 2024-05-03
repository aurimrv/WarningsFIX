#####################################################
#####    inserir dados da Checkstyle no BD      #####  
#####################################################
echo inserindo dados programa $2 da ferramenta Checkstyle no BD

JARS_DIR=$WARNINGSFIX_HOME/parser/Checkstyle/lib 
DIR_TMP=$WARNINGSFIX_HOME/tmp					   
cd $JARS_DIR

ls *.jar > $DIR_TMP/libs-jar-checkstyle.txt

JARS="";

for JAR in `cat $DIR_TMP/libs-jar-checkstyle.txt` 
do JARS=$JARS:$JARS_DIR/$JAR
done

cd $WARNINGSFIX_HOME/parser/Checkstyle/bin
CLASSPATH=$CLASSPATH:$JARS
XML=$WARNINGSFIX_HOME/parser/Checkstyle/src/main/META-INF/persistence.xml
CLASSPATH=$CLASSPATH:$XML

java -cp $CLASSPATH br.inf.ufg.es.vv.checkstyle.parser.Main $1 $2 $3

#####################################################
#####           Terminando                      #####  
#####################################################
