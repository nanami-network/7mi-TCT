# 7mi-TCT
ななみTCTのリポジトリ
ソースコードは自由に利用できますが、下記の許可されていること、禁止されていることについてはよく目を通してください。

# 許可されていること
- 個人利用
- ソースコードの利用、複製
- ソースコードの改良

# 禁止されていること
- このソースコードを悪意を持って利用する
- 配布

# 補足
- ない

# 無駄情報
どうでもいいけどNickStarblastは私です (ClockClap)


# 取得
[![Download Latest](https://img.shields.io/badge/Download-v3.5.2-green.svg)](https://github.com/nanami-network/7mi-TCT/releases/download/v3.5.2/nanami-tct-api-3.5.2.jar)

プラグインのバージョンは必ず1.12.2にしてください

まずは7mi-TCTをダウンロードして、どこかに保存しておきましょう。
(今回は例として `C:/Users/tutorial/nanamitct/` にjarファイルを置きます)

##Maven
**pom.xmlにこれを追加**

```xml
<dependencies>
    <dependency>
        <groupId>me.clockclap</groupId>
        <artifactId>nanami-tct-api</artifactId>
        <version>VERSION</version>
        <scope>system</scope>
        <systemPath>C:/Users/tutorial/nanamitct/nanami-tct-api-VERSION.jar</systemPath>
    </dependency>
</dependencies>
```

##Gradle
**build.gradleにこれを追加**

```
dependencies {
    compileOnly fileTree(dir: 'C:/Users/tutorial/nanamitct', include: ['nanami-tct-api-VERSION.jar'])
}
```
