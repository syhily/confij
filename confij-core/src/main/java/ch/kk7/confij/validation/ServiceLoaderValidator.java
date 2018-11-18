package ch.kk7.confij.validation;

import java.util.List;

import static ch.kk7.confij.common.ServiceLoaderUtil.maybeNewOf;

// NOT detectable by serviceLoader himself
public class ServiceLoaderValidator implements IValidator {
	private final List<IValidator> validators;

	public ServiceLoaderValidator() {
		validators = maybeNewOf(IValidator.class);
	}

	@Override
	public void validate(Object config) {
		validators.forEach(validator -> validator.validate(config));
	}
}
