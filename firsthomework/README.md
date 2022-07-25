# HAFTA-1
## Maven Custom Plugin
Arkadaşlar ödevinizde bir maven buildinde kullanılmak üzere custom plugin geliştirmenizi istiyorum.
Bu plugin çalıştırıldıgı projenin bazı bilgilerini pom.xml'den okuyup bir özet dosyası oluşturacaktır.
Bu pluginin bir tane goal'e sahip olacaktır. İsmi summarize olabilir.

Pluginimiz
projenin versiyonu
group id'si
artifact id'si
bağımlılıklarını
pluginlerini
developerlarını
ve property olarak verilmiş release.date isminde bir property'i okuyacaktır.

Ve bir adet outputFile adında bir parametre alacağız. Bu aldığımız özeti yazacağımız dosyanın adı olacaktır. (target folderının altına oluşturmasını bekliyorum dosyayı)

Ve bunları aşağıdaki formatta parametre olarak verilen output dosyasına yazacaktır.

Project info : groupId.artifactId.versiyon (buraya okudugunuz gerçek değerleri yazacaksınız)
Developers :
  Developer 1 Name : name
  Developer 2 Name : name (Kaç tane developer varsa artık)
Release Date : releaseDate
Dependencies
     Dependency : groupId.artifactId
     Dependency : groupId.artifactId (Kaç tane ise artık)
Plugins
     Plugin : artifactId
     Plugin : artifactId (Kaç tane ise artık)    
     
Daha sonra pluginimiz kendisini clean install diyerek build edip local repository'e yüklenmesini sağlayacağız. Daha sonra başka bir projede plugini pom.xml'e dahil edip plugini çalıştıracağız.

ÖDEVE DAIR NOT : 
Plugin geliştirmek için new maven project dedikten sonra archetype olarak org.apache.maven altında mojo archetype'ini seçiyoruz arkadaşlar.
