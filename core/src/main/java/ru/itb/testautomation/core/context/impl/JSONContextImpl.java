package ru.itb.testautomation.core.context.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.itb.testautomation.core.context.intf.JSONContext;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SuppressWarnings("unchecked")
public class JSONContextImpl extends JSONObject implements JSONContext {

    private static final Splitter SPLITTER = Splitter.on('.');
    private static final Pattern ARRAY_PATTERN = Pattern.compile("(\\w+)\\s*\\[\\s*(\\d+)\\s*\\]");
    private static final Pattern MAP_PATTERN = Pattern.compile("(\\w+)\\s*\\[\\s*(\"|')(\\w+)(\\2)\\s*\\]");

    private WeakHashMap<Object, Pair<Object, Object>> history = new WeakHashMap<>();
    private boolean collectHistory = false;

    @Override
    public Object get(Object key) {
        if (key instanceof String) {
            return iterateForGet(String.class.cast(key), false, true);
        } else {
            return super.get(key);
        }
    }

    @Override
    public void putAll(Map m) {
        if (collectHistory) {
            Set<Map.Entry> set = m.entrySet();
            for (Map.Entry entry : set) {
                if (this.containsKey(entry.getKey())) {
                    history.put(entry.getKey(), ImmutablePair.of(super.get(entry.getKey()), entry.getValue()));
                }
            }
        }
        super.putAll(m);
    }

    @Override
    public Object put(Object key, Object value) {
        Object toReturn;
        if (key instanceof String) {
            toReturn = iterateForPut(String.class.cast(key), value);
        } else {
            toReturn = super.put(key, value);
        }
        if (collectHistory) {
            history.put(key, ImmutablePair.of(toReturn, value));
        }
        return toReturn;
    }

    public Map<Object, Pair<Object, Object>> getHistory() {
        return Maps.newHashMap(history);
    }

    @Override
    public boolean containsKey(Object key) {
        if (key instanceof String) {
            return iterateForGet(String.class.cast(key), false, false) != null;
        } else {
            return super.containsKey(key);
        }
    }

    public Object create(Object key, boolean list) {
        Object o = list ? new JSONArray() : new JSONContextImpl();
        return put(key, o);
    }

    public Object create(Object key) {
        return create(key, false);
    }

    private Object iterateForGet(String stringKey, boolean checkLast, boolean doThrow) {
        Object tmpObject = this;
        Iterator<String> iterator = SPLITTER.split(stringKey).iterator();
        Matcher arrayMatcher = ARRAY_PATTERN.matcher("");
        Matcher mapMatcher = MAP_PATTERN.matcher("");
        do {
            String keyPart = iterator.next();
            if (checkLast && !iterator.hasNext()) {
                break;
            }
            if (tmpObject == null) {
                if (doThrow) {
                    throw new IllegalStateException(String.format("Cannot take property \"%s\" from null. Full key is '%s'", keyPart, stringKey));
                } else {
                    return null;
                }
            } else if (tmpObject instanceof Map) {
                if (arrayMatcher.reset(keyPart).matches()) {
                    String varName = arrayMatcher.group(1);
                    int index = Integer.parseInt(arrayMatcher.group(2));
                    Object o = Map.class.cast(tmpObject).get(varName);
                    if (o instanceof List) {
                        tmpObject = List.class.cast(o).get(index);
                    } else {
                        if (doThrow) {
                            throw new IllegalStateException(String.format("Cannot take index \"%s\" from object [%s], class is [%s]. Object is not list. Full key is '%s'", index, tmpObject, tmpObject.getClass().getSimpleName(), stringKey));
                        } else {
                            return null;
                        }
                    }
                } else if (mapMatcher.reset(keyPart).matches()) {
                    String varName = mapMatcher.group(1);
                    String subVarName = mapMatcher.group(3);
                    Object o = Map.class.cast(tmpObject).get(varName);
                    if (o instanceof Map) {
                        tmpObject = Map.class.cast(o).get(subVarName);
                    } else {
                        if (doThrow) {
                            throw new IllegalStateException(String.format("Cannot take property \"%s\" from object [%s], class is [%s]. Object is not map. Full key is '%s'", subVarName, tmpObject, tmpObject.getClass().getSimpleName(), stringKey));
                        } else {
                            return null;
                        }
                    }
                } else {
                    if (tmpObject == this) {
                        tmpObject = super.get(keyPart);
                    } else {
                        tmpObject = Map.class.cast(tmpObject).get(keyPart);
                    }
                }
            } else {
                if (doThrow) {
                    throw new IllegalStateException(String.format("Cannot take property \"%s\" from object [%s], class is [%s]. Object is not map. Full key is '%s'", keyPart, tmpObject, tmpObject.getClass().getSimpleName(), stringKey));
                } else {
                    return null;
                }
            }
        } while (iterator.hasNext());
        return tmpObject;
    }

    private Object iterateForPut(String key, Object value) {
        Object tmpObject = iterateForGet(key, true, true);
        Iterator<String> iterator = SPLITTER.split(String.class.cast(key)).iterator();
        String lastKey;
        do {
            lastKey = iterator.next();
        } while (iterator.hasNext());
        Matcher arrayMatcher = ARRAY_PATTERN.matcher(lastKey);
        Matcher mapMatcher = MAP_PATTERN.matcher(lastKey);
        String simpleName;
        if (tmpObject != null) {
            simpleName = tmpObject.getClass().getSimpleName();
        } else {
            simpleName = "null";
        }
        if (arrayMatcher.matches()) {
            String varName = arrayMatcher.group(1);
            int index = Integer.parseInt(arrayMatcher.group(2));
            if (tmpObject instanceof Map) {
                Object o = Map.class.cast(tmpObject).computeIfAbsent(varName, k -> new JSONArray());
                if (o instanceof List) {
                    List list = List.class.cast(o);
                    if (index == list.size()) {
                        list.add(value);
                        return null;
                    } else {
                        if (list.size() < index) {
                            ((JSONArray) list).ensureCapacity(index + 1); // it is need, if I want to put first key[2], key[1], key[0].
                            for (int number = list.size(); number < index + 1; number++) {
                                list.add(null);
                            }
                        }
                        Object toReturn = list.get(index);
                        list.set(index, value);
                        return toReturn;
                    }
                } else {
                    throw new IllegalStateException(String.format("Cannot set value by index \"%s\" to object [%s], class is [%s]. Object is not list. Full key is '%s'", index, o, o.getClass().getSimpleName(), key));
                }
            } else {
                throw new IllegalStateException(String.format("Cannot take property \"%s\" from object [%s], class is [%s]. Object is not map. Full key is '%s'", varName, tmpObject, simpleName, key));
            }
        } else if (mapMatcher.matches()) {
            String varName = mapMatcher.group(1);
            String subVarName = mapMatcher.group(3);
            if (tmpObject instanceof Map) {
                Object o = Map.class.cast(tmpObject).computeIfAbsent(varName, k -> new JSONContextImpl());
                if (o instanceof Map) {
                    return Map.class.cast(o).put(subVarName, value);
                } else {
                    throw new IllegalStateException(String.format("Cannot set property \"%s\" to object [%s], class is [%s]. Object is not map. Full key is '%s'", subVarName, o, o.getClass().getSimpleName(), key));
                }
            } else {
                throw new IllegalStateException(String.format("Cannot take property \"%s\" from object [%s], class is [%s]. Object is not map. Full key is '%s'", varName, tmpObject, simpleName, key));
            }
        } else {
            if (tmpObject instanceof Map) {
                if (tmpObject == this) {
                    return super.put(lastKey, value);
                } else {
                    return Map.class.cast(tmpObject).put(lastKey, value);
                }
            } else {
                throw new IllegalStateException(String.format("Cannot set property \"%s\" to object [%s], class is [%s]. Object is not map. Full key is '%s'", lastKey, tmpObject, simpleName, key));
            }
        }
    }

    public <T> T getCast(String key, Class<T> castTo) throws ClassCastException {
        Object o = get(key);
        return o != null ? castTo.cast(o) : null;
    }

    public <T> T get(String key, Class<T> castTo) {
        try {
            return getCast(key, castTo);
        } catch (ClassCastException e) {
            return null;
        }
    }

    public void merge(Map from) {
        mergeMap(this, from);
    }

    private void mergeMap(Map to, Map from) {
        for (Object o : from.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            Object fromValue = entry.getValue();
            Object fromKey = entry.getKey();
            if (to.containsKey(fromKey)) {
                Object toValue = to.get(fromKey);
                if (toValue instanceof Map && fromValue instanceof Map) {
                    mergeMap((Map) toValue, (Map) fromValue);
                } else if (toValue instanceof List && fromValue instanceof List) {
                    mergeList((List) toValue, (List) fromValue);
                } else {
                    to.put(fromKey, fromValue);
                }
            } else {
                to.put(fromKey, fromValue);
            }
        }
    }

    private void mergeList(List to, List from) {
        int toSize = to.size();
        int fromSize = from.size();
        for (int i = 0; i < toSize && i < fromSize; i++) {
            Object toValue = to.get(i);
            Object fromValue = from.get(i);
            if (toValue instanceof Map && fromValue instanceof Map) {
                mergeMap((Map) toValue, (Map) fromValue);
            } else if (toValue instanceof List && fromValue instanceof List) {
                mergeList((List) toValue, (List) fromValue);
            } else {
                to.set(i, fromValue);
            }
        }
        if (fromSize > toSize) {
            for (int i = toSize; i < fromSize; i++) {
                to.add(i, from.get(i));
            }
        }
    }

    public static <T extends JSONContextImpl> T fromJSON(String jsonString, Class<T> clazz) throws ParseException, IllegalAccessException, InstantiationException {
        JSONParser parser = new JSONParser();
        Object parse = parser.parse(jsonString);
        T instance = clazz.newInstance();
        if (parse instanceof Map) {
            instance.putAll((Map) parse);
        } else {
            instance.put("parsed", parse);
        }
        return instance;
    }

    public boolean isCollectHistory() {
        return collectHistory;
    }

    public JSONContextImpl setCollectHistory(boolean collectHistory) {
        this.collectHistory = collectHistory;
        return this;
    }
}
