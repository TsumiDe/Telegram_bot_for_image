from PIL import Image
from pathlib import Path
from rembg import remove


class Client:

    def __init__(self):
        self.list_of_extension = ["*.png", "*.jpg", "*.jpeg"]
        self.files = []

    def main(self):
        for extension in self.list_of_extension:
            self.files.extend(Path("E:\\projects\\TelegramAppBot\\src\\main\\inputImages").glob(extension))
        for file in self.files:
            input_path = Path(file)
            file_name = input_path.stem
            output_path = f"E:\\projects\\TelegramAppBot\\src\\main\\outputImages/{file_name}.png"
            input_image = Image.open(input_path)
            output_image = remove(input_image)
            output_image.save(output_path)


if __name__ == '__main__':
    Client().main()
