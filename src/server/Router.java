package server;

import java.lang.reflect.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import common.StreamUtils;


public class Router<T> {
	protected T controller;
	protected HashMap<Integer, Method> routingMap;
	
	public Router(T controller) throws InvalidRouteException {
		this.controller = controller;
		routingMap = buildMap();
	}
	
	public void route(MessageContext context, int code, int[] sizes, ByteBuffer content) throws NotFoundException, InvalidContentException {
		ByteBuffer contentCopy = content.slice();
		
		Method controllerRoute = routingMap.getOrDefault(code, null);
		if(controllerRoute == null)
			throw new NotFoundException("Expected route definiton (" + code + ").");
		
		Object[] paramValues = new Object[controllerRoute.getParameterCount()]; 
		
		if(sizes.length == 0) {
			if(paramValues.length > 0)
				paramValues[0] = context;
			try {
				controllerRoute.invoke(controller, paramValues);
			} catch (Exception ex) {
				throw new InvalidContentException(-1);
			}
			return;
		}
		
		int index = 0;
		int sizeIndex = 0;
		for(Parameter param : controllerRoute.getParameters()) {
			
			if(!content.hasRemaining())
				throw new InvalidContentException(sizeIndex);
			
			if(param.getType() == MessageContext.class) {
				paramValues[index++] = context;
				continue;
			} else if(param.getType() == int.class) {
				if(content.remaining() < Integer.BYTES)
					throw new InvalidContentException(sizeIndex);
				
				paramValues[index] = contentCopy.getInt();

			} else if (param.getType() == String.class) {
				paramValues[index] = decodeString(contentCopy, sizes[sizeIndex]);
			}
			
			index++;
			sizeIndex++;
		}
		
		try {
			controllerRoute.invoke(controller, paramValues);
		} catch (Exception ex) {
			throw new InvalidContentException(-1);
		}
	}
	
protected HashMap<Integer, Method> buildMap() throws InvalidRouteException {
	HashMap<Integer, Method> map = new HashMap<Integer, Method>();
	for (Method method : controller.getClass().getMethods()) {
		Route[] routeAnnotations = method.getAnnotationsByType(Route.class);
		if(routeAnnotations.length <= 0)
			continue;
		
		for(Route route : routeAnnotations) {
			if(map.containsKey(route.code())) {
				throw new InvalidRouteException("Found duplicate route definition (" + route.code() + ") while attempting to build Controller map.");
			}
			
			Parameter[] parameters = method.getParameters();
			boolean foundContext = false;
			for(Parameter param : parameters) {
				if(MessageContext.class == param.getType()) {
					if(foundContext) {
						throw new InvalidRouteException("Route definition cannot contain duplicate parameters with type MessageContext.");
					}
					
					foundContext = true;
				}
				if(MessageContext.class != param.getType() && int.class != param.getType() && String.class != param.getType()) {
					throw new InvalidRouteException("Route definition parameters may contain only MessageContext, int and String types.");
				}
			}
			
			
			map.put(route.code(), method);
		}
	}
	
	return map;
}
	
	private String decodeString(ByteBuffer content, int size) {
		byte[] bytes = new byte[size];
		content.get(bytes);
		return new String(bytes, StandardCharsets.UTF_8);
	}
}
