package csse374.revengd.soot;

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

import csse374.revengd.soot.SceneBuilder;

public class SceneBuilderTest {
	SceneBuilder builder;
	
	@Before
	public void setUp() throws Exception {
		this.builder = SceneBuilder.create();
		
		// Needed for running tests from Gradle
		Path buildResourcesTestPath = Paths.get("build", "resources", "test");
		if(!buildResourcesTestPath.toFile().exists()) {
			buildResourcesTestPath.toFile().mkdirs();
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreate() {
		assertNotNull(builder);
	}

	@Test
	public void testAddClassPath() {
		List<String> classPaths = Arrays.asList("abc", "def");		
		classPaths.forEach(path -> builder.addClassPath(path));
		
		assertEquals(classPaths, builder.classPaths);
	}

	@Test
	public void testAddClassPaths() {
		List<String> classPaths = Arrays.asList("abc", "def");		
		builder.addClassPaths(classPaths);
		
		assertEquals(classPaths, builder.classPaths);
	}

	@Test
	public void testAddClass() {
		List<String> classes = Arrays.asList("abc", "def");		
		classes.forEach(clazz -> builder.addClass(clazz));
		
		assertEquals(classes, builder.classes);
	}

	@Test
	public void testAddClasses() {
		List<String> classes = Arrays.asList("abc", "def");		
		builder.addClasses(classes);
		
		assertEquals(classes, builder.classes);
	}
	
	@Test
	public void testAddDirectory() {
		List<String> dirs = Arrays.asList("abc", "def");		
		dirs.forEach(dir -> builder.addDirectory(dir));
		
		assertEquals(dirs, builder.dirsToProcess);
	}

	@Test
	public void testAddDirectories() {
		List<String> dirs = Arrays.asList("abc", "def");		
		builder.addDirectories(dirs);
		
		assertEquals(dirs, builder.dirsToProcess);
	}

	@Test
	public void testSetEntryClass() {
		String entryClass = "package.Class";

		builder.setEntryClass(entryClass);
		assertEquals(entryClass, builder.entryClassToLoad);
	}

	@Test
	public void testAddEnterPointMatcher() {
		List<IEntryPointMatcher> matchers = Arrays.asList(mock(IEntryPointMatcher.class), mock(IEntryPointMatcher.class));
		matchers.forEach(matcher -> builder.addEntryPointMatcher(matcher));
		
		assertEquals(matchers, builder.matchers);
	}

	@Test
	public void testAddEnterPointMatchers() {
		List<IEntryPointMatcher> matchers = Arrays.asList(mock(IEntryPointMatcher.class), mock(IEntryPointMatcher.class));
		builder.addEntryPointMatchers(matchers);
		
		assertEquals(matchers, builder.matchers);
	}
	
	@Test
	public void testAddExclusion() {
		List<String> exclusions = Arrays.asList("abc.*", "def.abc.*");
		exclusions.forEach(exclusion -> builder.addExclusion(exclusion));
		
		assertEquals(exclusions, builder.exclusions);
	}

	@Test
	public void testAddExclusions() {
		List<String> exclusions = Arrays.asList("abc.*", "def.abc.*");
		builder.addExclusions(exclusions);
		
		assertEquals(exclusions, builder.exclusions);
	}

}
