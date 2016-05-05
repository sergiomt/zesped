<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
<div>
<div style="padding:0.2em 0 0 0;height:20px;color:#b72222;font-weight:bold;">
<xsl:value-of select="response/header" /> -
</div>
<xsl:for-each select="response/errors/error">
<div style="padding:0.2em 0 0 0;height:20px;"><xsl:value-of select="." /></div>
</xsl:for-each>
</div>
</xsl:template>
</xsl:stylesheet>