package server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class DemoSocketFactory {
	private static final String ksType = "JKS";
	private static final String ksAlgorithm = "SunX509";
	private static final String sslContextType = "TLS";

	private SSLServerSocketFactory factory;
	
	public DemoSocketFactory(String sslKeystorePath, String password) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, FileNotFoundException, IOException, KeyManagementException {
		KeyStore keyStore = KeyStore.getInstance(ksType);
		keyStore.load(new FileInputStream(sslKeystorePath), password.toCharArray());
		
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(ksAlgorithm);
		keyManagerFactory.init(keyStore, password.toCharArray());
		
		SSLContext sslContext = SSLContext.getInstance(sslContextType);
		sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
		
		factory = sslContext.getServerSocketFactory();
	}
	
	public SSLServerSocket getSocket(int port) throws IOException {
		return (SSLServerSocket)factory.createServerSocket(port);
	}
}
