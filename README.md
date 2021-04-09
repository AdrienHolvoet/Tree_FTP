# Application «Tree FTP»

Adrien Holvoet  
24/01/2021

## Introduction

Cette application permet d'afficher sur la sortie standard d'un terminal l'arborescence d'un répertoire distant accessible via le protocole applicatif File Transfer Protocol (FTP)

## Comment l'utiliser?

La javadoc a déja été générée, elle est placée dans le dossier "doc/javadoc", il est possible de la regénérer dans le dossier target qui se créera automatiquement.  
Pour générer la Javadoc :  

```mvn javadoc:javadoc```

L'exécutable a déjà été généré et il se trouve dans le dossier doc/executable du projet.  
Il suffit donc de se placer dans ce directory et de lancer la commande : 


```
java -jar TreeFtp.jar ftpAdress (optional <username>) (optional <password>)
```

Le premier argument est l'adresse d'un serveur ftp distant, le second le nom d'utilisateur et le troisième le mot de passe. Les deux derniers sont optionnels.  
  
Il est également possible de regénérer le .jar en allant placer à la racine du projet et en lançant la commande maven :  
``` mvn package``` l'exécutable se trouvera dans le  répertoire "doc/executable" du projet.  
  
**Démo** : à la racine du projet le fichier *demo.mkv* est une vidéo démo d'utilisation de la commande *TreeFtp*. il faut donc se trouver dans le répertoire "doc/executable" où l'exécutable a été généré.  
- 1er test : ce test est effectué sur le serveur public ftp.free.fr dont l'arborescence est assez petite. On peut voir sur ce test une gestion des pannes car ce serveur FTP déconnecte le client lorsqu' il essaie de rentrer dans certains dossiers, lorsque cela arrive, TreeFtp affiche "Failed to retrieve directory listing." et reconnecte le socket tout en continuant l'affichage de l'arborescence en cours.

- 2ième test : ce test est effectué sur le serveur public ftp.ubuntu.com dont l'arborescence est gigantesque. Le test est arrêté après quelques minutes sinon il serait sans fin.


## Architecture

### Classes/Interfaces

- *FtpClientInterface* : permet de lancer le client *FtpClient* et celui-ci définit les principales méthodes nécessaires au bon fonctionnement du *FtpClient*
- *FtpClient* : classe utilisée pour la mise en oeuvre des commandes de la RFC 959 (FTP) afin de se connecter et naviguer dans un serveur FTP distant et  pour afficher l'arborescence de celui-ci.
-  *FileUtils* : classe Singleton permettant de gérer l'impression des dossiers/fichiers récupérés par *FtpClient* dans un format spécifique, de dinstinguer les fichiers des dossiers et  liens. Egalement d'obtenir le nom du fichier/dossier/lien.

### Gestion d'erreur

#### Catch
- Dans Main.java  :   
  -  catch (SocketTimeoutException e) : occure quand un socket est ouvert depuis trop longtemps, comme par exemple lorsque le serveur FTP distant a une trop grande arborescence.
  -  catch (SocketException e) : levé par le socket lorsqu’une erreur se produit sur le réseau.
- Dans FtpClient.java : 
    - catch (UnknownHostException e) : levé quand l'adresse IP du serveur FTP n'a pas pu être déterminée.
    - catch (ConnectException e) : levé lorsque le serveur FTP distant n'est pas en écoute et refuse la connexion.
    - catch (Exception e) : levé quand une erreur inconnue occure lors de la reconstition du channel d'échange de données.

#### Throw:

- Dans Main.java  :   
  -  throw new SocketTimeoutException : on stoppe le client lorque cette exception est jetée car le socket a été fermé.
  -  throw new SocketException : on stoppe le client car il n'y a plus de connexion résau donc impossible au client de contacter le sevreur distant.
- Dans FtpClient.java : 
    - throw new SocketNotAcceptedException : custom exception jetée lorsque le serveur renvoie un réponse inconnue lors de l'initialisation de la connexion du socket au serveur.
    - throw new BadCommandResponseException : custom exception jetée lorsque le seveur envoie un code d'erreur dans sa réponse à une commande de la RFC 959 (File Transfer Protocol).    
    Exemple : le socket envoie la commande PWD au serveur, si la réponse du serveur ne commence pas avec le code 257 on jette cette exception car ce n'est pas le comportement attendu.
    - throw new NoDataChannelException : custom exception jetée lorsque treeFtp n'a pas reçu toutes les données pour créer la canal d'échange de données et est donc dans l'impossibilité de récupérer les données.
    - throw new UnknownHostException : jeté lorsque l'exception UnknownHostException a été catch (voir Catch section).
    - throw new ConnectException : jeté lorsque  l'exception ConnectException a été catch (voir Catch section).

## Code samples

### Extrait 1

Cette méthode publique de *ClientFtp* est utilisée pour initialiser la connexion du soket avec le serveur et créer ainsi un canal pour l'échange des commandes (canal de contrôle).

``` 
public void connect() throws IOException {
	try {
		socket = new Socket(server, FTP_PORT);
		// Returns an output stream for transmitting the data to the socket
		osCommand = socket.getOutputStream();
		printerCommand = new PrintWriter(osCommand, true);
		// Return an input stream to receive the data from the socket
		clCommand = socket.getInputStream();
		readerClCommand = new BufferedReader(new InputStreamReader(clCommand));
		String response = readerClCommand.readLine();
		if (!response.startsWith("220 ")) {
			throw new SocketNotAcceptedException(...)
		}
		response = this.sendCommand(USER + this.user);
		if (!response.startsWith("331 ")) {
			throw new BadCommandResponseException("TreeFTP failed to connect with this username : " + response);
		}
		response = this.sendCommand(PASS + this.password);
		if (!response.startsWith("230 ")) {
			throw new BadCommandResponseException("TreeFTP failed to connect with this password : " + response);
		}
	} catch UnknownHostException e {...} 
      catch ConnectException e {...} 
}
``` 
### Extrait 2
Cette méthode est privée car utilisée seulement à l'intérieur de *ClientFtp*. Celle-ci permet de construire le canal d'échange de données avec l'adresse IP et le numéro de port récupérés par la commande PASV et de renvoyer un socket qui y est connecté.
   
 ``` 
	private Socket dataExchangeChannel() throws Exception {
		String response = this.sendCommand(PASV);
		if (!response.startsWith("227 ")) {
			throw new BadCommandResponseException("FreeFtp could not connect in passive mode: " + response);
		}
		int begin = response.indexOf('(');
		int end = response.indexOf(')', begin + 1);
		String dataExchange = response.substring(begin + 1, end);
		// the recovered String is of this form : (IP1,IP2,IP3,IP4,PORT1,PORT2)
		String[] datasExchangeArray = dataExchange.split(","); 
		String ip = null;
		int port = -1;
		try {
		    // IP Adress = IP1.IP2.IP3.IP4
			ip = datasExchangeArray[0] + "." + datasExchangeArray[1] + "." + datasExchangeArray[2] + "."
					+ datasExchangeArray[3];
			// PORT = (PORT1 x 256 ) + PORT2
			port = Integer.parseInt(datasExchangeArray[4]) * PORT_MULTIPLIER + Integer.parseInt(datasExchangeArray[5]);
		} catch (Exception e) {
			throw new NoDataChannelException("TreeFtp did not receive all the data to connect to the data channel.");
		}
		return new Socket(ip, port);
	}
``` 
### Extrait 3

Cette méthode est privée et est utilisée seulement à l'intérieur de *ClientFtp*. Celle-ci est une méthode récursive qui prend en paramètres le dossier courant à afficher et l'indentation qui lui est propre. Cette méthode utilise le canal de données pour récuperer le contenu du dossier en paramètre.
```
private void list(String directory, String indent) throws Exception {
		String response = null;
		if (directory != "") {
			this.sendCommand(CWD + directory);
		}
		Socket dataSocket = this.dataExchangeChannel();
		ArrayList<String> datas = new ArrayList<String>();
		this.sendCommand(LIST);
		clData = dataSocket.getInputStream();
		readerClData = new BufferedReader(new InputStreamReader(clData));
		while ((response = readerClData.readLine()) != null) {
			datas.add(response);
		}
		response = readerClCommand.readLine();
		// if the response is null it's mean that the server closed the connection
		// because it failed to retrieve directory listing
		// but we still want to see the others folder so we reconnect
		if (datas.size() == 0 || response == null) {
			String message = null;
			if (datas.size() == 0) {
				message = "Empty directory listing.";
			}
			if (response == null) {
				this.connect();
				message = "Failed to retrieve directory listing.";
			}
			FileUtils.printEmptyOrNotOpen(indent, message);
		}
		printTree(datas, indent, directory);
	}
```
### Extrait 4

Cette méthode est privée et est utilisée seulement à l'intérieur de *ClientFtp*. Elle est appelée à l'intérieur de la méthode *void list()*. Elle affiche le contenu du dossier passé en paramètre sous forme de tree et appelle récursivement la méthode *void list()* si il y a d'autres dossiers contenus à l'intérieur du dossier courant.
```
/**
	 * Display the contents of the directory passed as a parameter in the form of a tree
	 * 
	 * @throws Exception
	 * @param directory the path of the current directory listed
	 * @param indent the indentation related to the depth of the folder in the tree structure
	 * @param datas the data files/folder contained inside the directory parameter
	 */
	 private void printTree(ArrayList<String> datas, String indent, String directory) throws Exception {
		String name;
		for (String s : datas) {
			System.out.print(indent);
			name = FileUtils.getName(s);
			if (FileUtils.isDirectory(s)) {
				FileUtils.printDirectory(name);
				if (!name.equals(".") && !name.equals("..")) // to avoid infinite recursion on these folders (experiment
																// with on local windows ftp)
				{
					this.list(directory + "/" + FileUtils.getName(s), indent + INDENT);
				}
			} else if (FileUtils.isLink(s)) {
				FileUtils.printLink(name);
			} else {
				FileUtils.printFile(name);
			}
		}
	}
```	
### Extrait 5
  
Cette classe FileUtils est une classe construite selon le design pattern Singleton, ces méthodes sont toutes statiques et permettent l'affichage/distinction des fichiers/dossiers et liens .

    
``` 
public final class FileUtils {
	private FileUtils() {
	}
	public static void printDirectory(String folder) {
    	...
	}
	public static void printLink(String link) {
		...
	}
	public static void printFile(String file) {
		...
	}
	public static boolean isDirectory(String file) {
    	...
	}
	public static boolean isLink(String file) {
		...
	}
	public static String getName(String str) {
	    ...
	}
}
```
