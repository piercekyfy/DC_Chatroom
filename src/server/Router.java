package server;

import java.lang.reflect.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;


public class Router<T> {
	protected T controller;
	protected HashMap<Integer, Method> routingMap;
	
	public Router(T controller) throws InvalidRouteException {
		this.controller = controller;
		routingMap = buildMap();
	}
	
	public void route(MessageContext context, int code, int[] sizes, byte[] content) throws NotFoundException, InvalidContentException {
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
		int offset = 0;
		for(Parameter param : controllerRoute.getParameters()) {
			
			if(offset >= content.length)
				throw new InvalidContentException(sizeIndex);
			
			if(param.getType() == MessageContext.class) {
				paramValues[index] = context;
				index++;
				continue;
			} else if(param.getType() == int.class) {
				paramValues[index] = decodeInt(content, offset);

			} else if (param.getType() == String.class) {
				paramValues[index] = decodeString(content, offset, sizes[sizeIndex]);
			}
			
			offset += sizes[sizeIndex];
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
	
	// TODO: replace with common parse methods
	private int decodeInt(byte[] bytes, int offset) {
		return ByteBuffer.wrap(bytes, offset, Integer.BYTES).getInt();
	}
	
	private String decodeString(byte[] bytes, int offset, int size) {
		return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(bytes, offset, size)).toString();
	}
}
