package ch.kk7.confij.source.logical;

import ch.kk7.confij.ConfijBuilder;
import ch.kk7.confij.source.ConfijSource;
import ch.kk7.confij.source.ConfijSourceException;
import ch.kk7.confij.source.ConfijSourceTestBase;
import org.assertj.core.api.AbstractStringAssert;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrSourceTest extends ConfijSourceTestBase {

	private static AbstractStringAssert<?> assertThatX(ConfijSource one, ConfijSource or, ConfijSource... orEven) {
		ConfigX orConfig = ConfijBuilder.of(ConfigX.class)
				.loadFrom(setXTo("before"))
				.loadFrom(new OrSource(one, or, orEven))
				.build();
		return assertThat(orConfig.x());
	}

	@Test
	public void exceptionAtFirst() {
		assertThatX(alwaysFail, noop).isEqualTo("before");
	}

	@Test
	public void exceptionOnAll() {
		assertThatThrownBy(() -> assertThatX(alwaysFail, alwaysFail)).isInstanceOf(ConfijSourceException.class);
		assertThatThrownBy(() -> assertThatX(alwaysFail, alwaysFail, alwaysFail, alwaysFail)).isInstanceOf(ConfijSourceException.class);
	}

	@Test
	public void firstSucceeds() {
		assertThatX(setXTo("1st"), setXTo("2nd")).isEqualTo("1st");
	}

	@Test
	public void secondSucceeds() {
		assertThatX(alwaysFail, setXTo("2nd")).isEqualTo("2nd");
	}

	@Test
	public void secondSucceedsAndFirstLate() {
		assertThatX(setThenFail, setXTo("2nd")).isEqualTo("2nd");
	}

	@Test
	public void thirdSucceeds() {
		assertThatX(alwaysFail, alwaysFail, setXTo("3rd"), alwaysFail).isEqualTo("3rd");
	}

	@Test
	public void viaBuilder() {
		System.setProperty("app.x", "appx");
		ConfigX orConfig = ConfijBuilder.of(ConfigX.class)
				.loadFrom(setXTo("before"))
				.loadFromFirstOf("sys:app", "sys:fuu")
				.build();
		assertThat(orConfig.x()).isEqualTo("appx");
	}
}
