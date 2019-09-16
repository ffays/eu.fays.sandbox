package eu.fays.sandbox.jaxb.mapofmap3;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.NONE)
public class Dictionary {
	@XmlID
	@XmlAttribute
	private String id;

	@XmlAttribute
	private String key;

	@XmlElementWrapper(name = "items")
	@XmlElement(name = "item")
	private List<Item> items;

	private String buildId() {
		// IDREF is of type NCName
		// NCName definition: https://www.w3.org/TR/1999/WD-xmlschema-2-19990924/#NCName
		// Name definition : https://www.w3.org/TR/REC-xml/#d0e804
		final UUID uuid = randomUUID();
		final ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		bb.putLong(uuid.getMostSignificantBits());
		bb.putLong(uuid.getLeastSignificantBits());
		final String b64 = Base64.getEncoder().encodeToString(bb.array());
//		String result = b64.replace('+', '\u00D0').replace('/', '\u00D8').replace('=', '_');
		String result = ":" + b64.replace('+', '_').replace('/', ':').replace('=', ' ').trim();
		return result;
	}

	public Dictionary() {
		id = buildId();
	}

	public Dictionary(Entry<String, Map<String, Object>> entry) {
		this();
		setKey(entry.getKey());
		items = entry.getValue().entrySet().stream().map(Item::new).collect(toList());

	}

	public String getId() {
		if (id == null) {
			id = buildId();
		}
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public Stream<Dictionary> dictionaryStream() {
		final List<Dictionary> result = new ArrayList<>();
		final Deque<Item> stack = new ArrayDeque<>(getItems());
		result.add(this);

		while (!stack.isEmpty()) {
			final Item item = stack.pop();
			if (item.hasDictionary()) {
				result.add(item.getDictionary());
				stack.addAll(item.getDictionary().getItems());
			}
		}

		return result.stream();
	}
}
