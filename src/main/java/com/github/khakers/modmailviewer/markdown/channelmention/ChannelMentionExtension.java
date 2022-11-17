package com.github.khakers.modmailviewer.markdown.channelmention;

import com.github.khakers.modmailviewer.markdown.channelmention.internal.ChannelMentionInlineParserExtension;
import com.github.khakers.modmailviewer.markdown.channelmention.internal.ChannelMentionNodeRenderer;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.data.NullableDataKey;
import com.vladsch.flexmark.util.html.Attributes;
import org.jetbrains.annotations.NotNull;

public class ChannelMentionExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension {
    final public static NullableDataKey<String> CHANNEL_STYLE_HTML_OPEN = new NullableDataKey<>("CHANNEL_STYLE_HTML_OPEN");
    final public static NullableDataKey<String> CHANNEL_STYLE_HTML_CLOSE = new NullableDataKey<>("CHANNEL_STYLE_HTML_CLOSE");

    final public static DataKey<Attributes> EXTRA_CHANNEL_ATTRIBUTES =  new DataKey<>("EXTRA_CHANNEL_ATTRIBUTES", Attributes.EMPTY);


    private ChannelMentionExtension() {

    }

    public static ChannelMentionExtension create() {
        return new ChannelMentionExtension();
    }

    /**
     * This method is called first on all extensions so that they can adjust the options that must be
     * common to all extensions.
     *
     * @param options option set that will be used for the builder
     */
    @Override
    public void rendererOptions(@NotNull MutableDataHolder options) {

    }

    /**
     * This method is called first on all extensions so that they can adjust the options that must be common to all extensions.
     *
     * @param options option set that will be used for the builder
     */
    @Override
    public void parserOptions(MutableDataHolder options) {

    }

    /**
     * Called to give each extension to register extension points that it contains
     *
     * @param htmlRendererBuilder builder to call back for extension point registration
     * @param rendererType        type of rendering being performed. For now "HTML", "JIRA" or "YOUTRACK"
     */
    @Override
    public void extend(HtmlRenderer.@NotNull Builder htmlRendererBuilder, @NotNull String rendererType) {
        if (htmlRendererBuilder.isRendererType("HTML")) {
            htmlRendererBuilder.nodeRendererFactory(new ChannelMentionNodeRenderer.Factory());
        }
    }

    /**
     * This method is called on all extensions so that they can register their custom processors
     *
     * @param parserBuilder parser builder with which to register extensions
     */
    @Override
    public void extend(Parser.Builder parserBuilder) {
        parserBuilder.customInlineParserExtensionFactory(new ChannelMentionInlineParserExtension.Factory());
    }

    @Override
    public String toString() {
        return "ChannelMentionExtension{}";
    }
}
