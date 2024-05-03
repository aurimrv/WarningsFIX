for OUT in $(find ~/workspace/WarningsFIX/programs_analyzeds -name outputs)
do
rm -rf $OUT
done

rm -rf ~/workspace/WarningsFIX/tmp/*
#cp -r  ~/workspace/WarningsFIX/TiposdeAvisos/* ~/workspace/WarningsFIX/tmp/

psql -h localhost -U postgres -d conquest -f scripts/erasealldb.sql

#echo Diretórios e BD limpo
