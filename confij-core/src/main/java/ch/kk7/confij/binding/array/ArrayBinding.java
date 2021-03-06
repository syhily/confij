package ch.kk7.confij.binding.array;

import ch.kk7.confij.binding.BindingResult;
import ch.kk7.confij.binding.BindingType;
import ch.kk7.confij.binding.ConfigBinder;
import ch.kk7.confij.binding.ConfigBinding;
import ch.kk7.confij.binding.collection.CollectionUtil;
import ch.kk7.confij.tree.NodeDefinition.NodeDefinitionList;
import ch.kk7.confij.tree.NodeBindingContext;
import ch.kk7.confij.tree.ConfijNode;
import com.fasterxml.classmate.ResolvedType;
import lombok.ToString;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@ToString
public class ArrayBinding<T> implements ConfigBinding<Object> {
	private final ResolvedType componentType;
	private final ConfigBinding<T> componentDescription;

	public ArrayBinding(BindingType bindingType, ConfigBinder configBinder) {
		componentType = bindingType.getResolvedType();
		//noinspection unchecked
		componentDescription = (ConfigBinding<T>) configBinder.toConfigBinding(bindingType);
	}

	@Override
	public NodeDefinitionList describe(NodeBindingContext nodeBindingContext) {
		return new NodeDefinitionList(nodeBindingContext, componentDescription.describe(nodeBindingContext));
	}

	/**
	 * binds to Object instead of T[] since it also handles primitive arrays
	 */
	@Override
	public BindingResult<Object> bind(ConfijNode config) {
		List<BindingResult<?>> bindingResultChildren = new ArrayList<>();
		// TODO: add config to allow null values in array
		List<ConfijNode> childNodes = CollectionUtil.childrenAsContinuousList(config);
		Object result = Array.newInstance(componentType.getErasedType(), childNodes.size());
		int i = 0;
		for (ConfijNode childNode : childNodes) {
			BindingResult<?> item = componentDescription.bind(childNode);
			Array.set(result, i++, item.getValue());
			bindingResultChildren.add(item);
		}
		return BindingResult.of(result, config, bindingResultChildren);
	}
}
