# ChatSettings
---

ChatSettings is a lightweight mod for minecraft that adds customizable chat settings
Supports fabric mod loader only!

## features

### Chat filter

- supports **regex** for filtering.

#### Blacklist

- add a filter to stop text that contains the added phrases from appearing in chat.
- to add a blacklist filter, type `/cs blacklist add <line>` in chat.
- to remove from the blacklist, type `/cs blacklist remove <line>`.

#### Whitelist
- Messages that contain any Whitelist phrases will always show, even when it matches the blacklist filter.
- to add a whitelist filter, type `/cs whitelist add <line>`.
- to remove from the whitelist, type `/cs whitelist remove <line>`.

The filter does not need to be an exact match, the text only has to contain the filter line.

---

## License

Chat settings is under the [MIT License](LICENSE)