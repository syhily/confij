package ch.kk7.confij.docs;

import ch.kk7.confij.annotation.Default;
import ch.kk7.confij.annotation.Key;
import ch.kk7.confij.pipeline.ConfijBuilder;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Source extends DocTestBase {
	// tag::interface[]
	interface ServerConfig {
		String name();

		URL externalUrl();

		@Default("1")
		int line();

		@Default("30s")
			// <1>
		Duration timeout();
	}
	// end::interface[]

	@Test
	public void pipedSources() {
		System.setProperty("app.line", "3");
		// tag::pipedsource[]
		ServerConfig serverConfig = ConfijBuilder.of(ServerConfig.class)
				.withSource("classpath:generic.properties") // <2>
				.withSource("server.properties") // <3>
				.withSource("sys:app") // <4>
				.build();
		// end::pipedsource[]
		assertThat(serverConfig.toString()).isEqualToIgnoringWhitespace(classpath("pipedsource.txt"));
	}

	// tag::defaults[]
	interface HasDefaults {
		@Default("a default value")
		String aString();

		@Default("23")
		int aNumber();

		default List<Boolean> aListOfBooleans() {
			return Arrays.asList(true, false);
		}

		default int aNumberPlus1() {
			return aNumber() + 1;
		}
	}
	// end::defaults[]

	@Test
	public void defaultValues() {
		HasDefaults defaults = ConfijBuilder.of(HasDefaults.class)
				.build();
		assertThat(defaults.aString()).isEqualTo("a default value");
		assertThat(defaults.aNumber()).isEqualTo(23);
		assertThat(defaults.aListOfBooleans()).containsExactly(true, false);
		assertThat(defaults.aNumberPlus1()).isEqualTo(24);
	}

	// tag::nestedinterface[]
	interface Config {
		String key();
		Nested nest(); // <1>
		List<Nested> listOfNest(); // <2>
		Map<String, Nested> mapOfNest(); // <3>
	}

	interface Nested {
		int x();
		int y();
	}
	// end::nestedinterface[]

	@Test
	public void nestedPropertiesFile() {
		Config config = ConfijBuilder.of(Config.class)
				.withSource("nested.properties")
				.build();
		assertThat(config.key()).isEqualTo("value");
		assertThat(config.nest().x()).isEqualTo(0);
		assertThat(config.nest().y()).isEqualTo(1);
		assertThat(config.listOfNest()).hasSize(3);
		assertThat(config.mapOfNest()).containsOnlyKeys("mykey", "myotherkey")
				.hasEntrySatisfying("myotherkey", nested -> assertThat(nested.y()).isEqualTo(0));
	}

	@Test
	public void powerOfTheAnySource() {
		System.setProperty("some.prefix.key", "fromSysprops");
		// tag::anysource[]
		ConfijBuilder.of(Config.class)
				.withSource("nested.properties") // properties file in current working directory
				.withSource(new File("nested.properties").getAbsolutePath()) // equivalent
				.withSource("file:nested.properties") // equivalent
				.withSource("classpath:nested${key}.yaml") // a YAML file on the classpath root
				.withSource("${key}:nestedvalue.yaml") // ...with variable replacements
				.withSource("sys:some.prefix") // from system properties
				.withSource("env:some_prefix") // from environment variables
				.build();
		// end::anysource[]
	}

	interface ComlexYaml {
		List<String> listOfStrings();
		Map<String, Integer> mapOfIntegers();
		Map<String, Integer> mapOfIntegersClone();
		LinkedList<String> anotherList();
		Date date();
		@Key("true") boolean isTrue();
		@Key("false") boolean isFalse();
	}

	@Test
	public void complexYaml() {
		ComlexYaml yaml = ConfijBuilder.of(ComlexYaml.class)
				.withSource("complex.yaml")
				.build();
		assertThat(yaml.listOfStrings()).containsExactly("String", "String on a single line", "Double quotation marks\t");
	}
}