def main():
    f = open("errorfile", "r")
    msg = f.read()

    msg = msg.replace("\n", "\\n").replace("\"", "\\\"").replace("\t", "\\t")

    print "{\"fractionalScore\": 0.0, \"feedback\": \"Compile error: %s\"}" % msg

if __name__ == "__main__":
    main()
