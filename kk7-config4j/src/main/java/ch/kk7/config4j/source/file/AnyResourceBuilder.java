package ch.kk7.config4j.source.file;

import ch.kk7.config4j.source.ConfigSourceBuilder;
import ch.kk7.config4j.source.file.format.PropertiesFormat;
import ch.kk7.config4j.source.file.format.ResourceFormat;
import ch.kk7.config4j.source.file.format.YamlFormat;
import ch.kk7.config4j.source.file.resource.ClasspathResource;
import ch.kk7.config4j.source.file.resource.Config4jResource;
import ch.kk7.config4j.source.file.resource.FileResource;
import ch.kk7.config4j.source.file.resource.URLResource;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AnyResourceBuilder implements ConfigSourceBuilder {
	private final List<Config4jResource> supportedResources;
	private final List<ResourceFormat> supportedFormats;

	public AnyResourceBuilder() {
		supportedResources = new ArrayList<>(Arrays.asList(new ClasspathResource(), new FileResource(), new URLResource()));
		supportedFormats = new ArrayList<>(Arrays.asList(new PropertiesFormat(), new YamlFormat()));
	}

	@Override
	public Optional<FixedResourceSource> fromURI(URI path) {
		Optional<Config4jResource> resource = supportedResources.stream()
				.filter(r -> r.canHandle(path))
				.findFirst();
		if (!resource.isPresent()) {
			return Optional.empty();
		}
		return supportedFormats.stream()
				.filter(r -> r.canHandle(path))
				.findFirst()
				.map(format -> new FixedResourceSource(path, resource.get(), format));
	}
}