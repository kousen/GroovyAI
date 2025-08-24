package com.kousenit

enum SummaryType {
    AUTO('auto'), CONCISE('concise'), DETAILED('detailed')

    final String value

    SummaryType(String value) { this.value = value }
}