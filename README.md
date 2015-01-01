<h1>Website monitoring</h1>

<h2>Standalone application (with HSQL database)</h2>

<p>Just download <a href="https://sourceforge.net/projects/sitemonitoring/files/latest/download?source=files">compiled file</a>
<br />
Next run: <code>java -jar sitemonitoring.jar</code></p>
</p>

<p>OR run: <code>mvn clean package -P demo</code>
<br />
Next run: <code>java -jar target/sitemonitoring.war</code></p>

<h2>Development (with embedded HSQL database)</h2>

<p>To run in development mode: <code>mvn jetty:run -P dev</code></p>

<h2>Heroku (with PostgreSQL database)</h2>

<p>To deploy on Heroku change in pom.xml: <code>&lt;argument&gt;sitemonitoring&lt;/argument&gt;</code> "sitemonitoring" to your application name in Heroku and run: <code>mvn clean install -P heroku</code>

