function request()
   local str1 = 'This is a string.'
   return wrk.format("GET", "/test", {}, str1)
end