/**
 * 
 */
package org.zmonitor.selector.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.zmonitor.selector.Entry;
import org.zmonitor.selector.impl.EntryLocalProperties;
import org.zmonitor.selector.impl.MatchCtx;
import org.zmonitor.selector.impl.MatchCtxCtrl;
import org.zmonitor.selector.impl.PseudoClassDef;
import org.zmonitor.selector.impl.model.Selector;
import org.zmonitor.selector.impl.model.SimpleSelectorSequence;


/**
 * A wrapper of Component, providing a context for selector matching algorithm.
 * @since 6.0.0
 * @author simonpai
 */
public class MatchCtxImpl<E> implements MatchCtx<E> {
	
	private MatchCtx<E> _parent;
	private Entry<E> _comp;
	
	

	
	
}
