/*******************************************************************************
 * Copyright (c) 2014-2017 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
scout.SmartField2Popup = function() {
  scout.SmartField2Popup.parent.call(this);
  this.animateRemoval = true;
};
scout.inherits(scout.SmartField2Popup, scout.Popup);

scout.SmartField2Popup.prototype._init = function(options) {
  options.withFocusContext = false;
  scout.SmartField2Popup.parent.prototype._init.call(this, options);
  this.proposalChooser = this._createProposalChooser();
  this.proposalChooser.on('lookupRowSelected', this._triggerEvent.bind(this));
  this.proposalChooser.on('activeFilterSelected', this._triggerEvent.bind(this));
};

scout.SmartField2Popup.prototype._createProposalChooser = function() {
  var objectType = this._smartField().browseHierarchy ? 'TreeProposalChooser2' : 'TableProposalChooser2';
  return scout.create(objectType, {
    parent: this
  });
};

scout.SmartField2Popup.prototype._smartField = function() {
  return this.parent;
};

scout.SmartField2Popup.prototype._smartFieldBounds = function() {
  return scout.graphics.offsetBounds(this.parent.$field);
};

/**
 * @override
 */
scout.SmartField2Popup.prototype._createLayout = function() {
  if (this._smartField().variant === scout.SmartField2.DisplayStyle.DROPDOWN) {
    return new scout.DropdownPopupLayout(this, this.proposalChooser);
  } else {
    return new scout.SmartField2PopupLayout(this, this.proposalChooser);
  }
};

scout.SmartField2Popup.prototype._render = function() {
  var cssClass = this._smartField().cssClassPrefix() + '-popup';
  scout.SmartField2Popup.parent.prototype._render.call(this);
  this.$container
    .addClass(cssClass)
    .on('mousedown', this._onContainerMouseDown.bind(this));
  this.proposalChooser.render();
};

scout.SmartField2Popup.prototype.setLookupResult = function(result) {
  this.proposalChooser.setLookupResult(result);
};

// FIXME [awe] 7.0 - SF2: check if this function is still required when ENTER keystroke is handled properly
scout.SmartField2Popup.prototype.getSelectedLookupRow = function() {
  return this.proposalChooser.getSelectedLookupRow();
};

scout.SmartField2Popup.prototype.setStatus = function(status) {
  this.proposalChooser.setStatus(status);
};

scout.SmartField2Popup.prototype.setStatusLookupInProgress = function(status) {
  this.proposalChooser.setStatus(scout.Status.ok({
    message: this.session.text('searchingProposals')
  }));
};

scout.SmartField2Popup.prototype.selectFirstLookupRow = function() {
  this.proposalChooser.selectFirstLookupRow();
};

scout.SmartField2Popup.prototype.selectLookupRow = function() {
  this.proposalChooser.triggerLookupRowSelected();
};

/**
 * Delegates the key event to the proposal chooser.
 */
scout.SmartField2Popup.prototype.delegateKeyEvent = function(event) {
  event.originalEvent.smartFieldEvent = true;
  this.proposalChooser.delegateKeyEvent(event);
};

scout.SmartField2Popup.prototype._triggerEvent = function(event) {
  this.trigger(event.type, event);
};

/**
 * This event handler is called before the mousedown handler on the _document_ is triggered
 * This allows us to prevent the default, which is important for the CellEditorPopup which
 * should stay open when the SmartField popup is closed. It also prevents the focus blur
 * event on the SmartField input-field.
 */
// TODO [awe] 7.0 - SF2: check if still required --> see SmartField2Popup.js
scout.SmartField2Popup.prototype._onContainerMouseDown = function(event) {
  // when user clicks on proposal popup with table or tree (prevent default,
  // so input-field does not lose the focus, popup will be closed by the
  // proposal chooser impl.
  return false;
};
