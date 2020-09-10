# Обфускирования и де-обфускирования данных

## Цель

Необходимо написать программу, которая использует BouncyCastle для шифрования и дешифрования данных. 
Также необходимо осуществить подпись данных сертификатом и последующую верификацию этих данных.

## Использование

Соберите проект:

```bash
$ gradle build
```

Выполните следующую команду, чтобы создать обфусцированный XML файл:
```bash
$ java -jar build/libs/lab1-1.0-SNAPSHOT.jar -m "obf" -i "path to an input file" -o "path to an output file"
```

Выполните следующую команду, чтобы деобфусцировать XML файл:
```bash
$ java -jar build/libs/lab1-1.0-SNAPSHOT.jar -m "obf" -i "path to an input file" -o "path to an output file"
```