package ch.kk7.confij.binding.leaf.mapper;

import ch.kk7.confij.annotation.ValueMapper;
import ch.kk7.confij.binding.BindingType;
import ch.kk7.confij.binding.leaf.ValueMapperInstance;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class DateMapper extends AbstractClassValueMapper<Date> {
	@Inherited
	@Retention(RUNTIME)
	@Target({ElementType.METHOD, ElementType.TYPE})
	@ValueMapper(DateMapper.class)
	public @interface Type {
		String value() default "yyyy-MM-dd'T'HH:mm:ss.SXXX";
	}

	@Type
	private static final class AnnonHolder {
	}

	@RequiredArgsConstructor
	public static class DateMapperInstance implements ValueMapperInstance<Date> {
		@NonNull
		private final DateTimeFormatter dateTimeFormatter;

		@Override
		public Date fromString(String string) {
			OffsetDateTime offsetDateTime = OffsetDateTime.parse(string, dateTimeFormatter);
			return Date.from(offsetDateTime.toInstant());
		}
	}

	public DateMapper() {
		super(Date.class);
	}

	@Override
	public ValueMapperInstance<Date> newInstance(BindingType bindingType) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(bindingType.getBindingSettings()
				.getFactoryConfigFor(DateMapper.class)
				.filter(Type.class::isInstance)
				.map(Type.class::cast)
				.orElse(AnnonHolder.class.getAnnotation(Type.class))
				.value());
		return new DateMapperInstance(dateTimeFormatter);
	}
}