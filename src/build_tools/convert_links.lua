function Link(el)
  if el.target:match("%.md$") then
    el.target = el.target:gsub("%.md$", ".html")
  end
  return el
end
